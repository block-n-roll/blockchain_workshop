package org.blocknroll.blockchain.workshop;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class NodeMain extends AbstractActor {

  private final static String HELP = "\n".join(
      "Usage:",
      "   COMMAND [OPTIONS] [ARGUMENTS]",
      "",
      "Commands:",
      "   start [PORT [PEER_NODE]]",
      "      Starts a local node listening to PORT for commands. Optionally it joins to PEER_NODE.",
      "   stop [NODE]",
      "      Stops a local or remote NODE (use with care!).",
      "   join [NODE] [PEER_NODE]",
      "      Makes NODE to leave current cluster and to join to PEER_NODE.",
      "   leave [NODE]",
      "      Makes NODE to leave its current cluster but keeps it running.",
      "   mine-facts [NODE] [FACTS...]",
      "      Mines a new block in NODE from given facts.",
      "   get-chain [NODE]",
      "      Retrieves the chain from NODE.",
      "   update-chain [NODE]",
      "      Forces NODE to update its chain.",
      "   status [NODE]",
      "      Displays status information for NODE, including its current block.",
      "",
      "Options:",
      "   -h\tPrints this help and exists.",
      "",
      "Notes:",
      "   All NODES are in the form IP:PORT, assuming localhost when IP is not given.");
  private final static String DEFAULT_HOST = "127.0.0.1";
  private final static int DEFAULT_PORT = 2550;

  // node comands
  enum Command {
    START, STOP, JOIN, LEAVE, MINE_FACTS, GET_CHAIN, UPDATE_CHAIN, STATUS
  }

  private static class NodeAddress {

    public NodeAddress() {
      host = DEFAULT_HOST;
      port = DEFAULT_PORT;
    }

    public NodeAddress(String address) {
      if (address.contains(":")) {
        String[] pair = address.trim().split(":");
        host = (pair[0].equalsIgnoreCase("localhost") ? DEFAULT_HOST : pair[0]);
        port = Integer.parseInt(pair[1]);
      } else if (address.contains(".")) {
        host = address.trim();
        port = DEFAULT_PORT;
      } else {
        host = DEFAULT_HOST;
        port = Integer.parseInt(address.trim());
      }
    }

    public String toString() {
      return host + ":" + port;
    }

    public String host;
    public int port;
  }

  // cluster objects
  private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
  private Cluster cluster = Cluster.get(getContext().system());

  // input arguments
  private static Command command = null;
  private static NodeAddress nodeAddress = null;
  private static NodeAddress peerAddress = null;
  private static String facts = null;

  //subscribe to cluster changes
  @Override
  public void preStart() {
    cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(),
        MemberEvent.class, UnreachableMember.class);
  }

  //re-subscribe when restart
  @Override
  public void postStop() {
    cluster.unsubscribe(self());
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(String.class, msg -> {
              System.out.println("-------------------------------------------------");
              System.out.println(msg);
              System.out.println("-------------------------------------------------");
            }
        )
        .match(NodeMessages.Join.class, msg -> {
          System.out.println("Joining to " + msg.toString() + "...");
          Address address = new Address("akka.tcp", "ClusterSystem", msg.host, msg.port);
          cluster.join(address);
        })
        .match(NodeMessages.Leave.class, msg -> {
          System.out.println("Leaving cluster...");
          cluster.leave(cluster.selfAddress());
        })
        /*
        .match(MemberUp.class, mUp -> {
          log.info("Member is Up: {}", mUp.member());
        })
        .match(UnreachableMember.class, mUnreachable -> {
          log.info("Member detected as unreachable: {}", mUnreachable.member());
        })
        .match(MemberRemoved.class, mRemoved -> {
          log.info("Member is Removed: {}", mRemoved.member());
        })
        .match(MemberEvent.class, message -> {
          log.info(message.toString());
          // ignore
        })*/
        .build();
  }

  private static ActorRef startup() {
    // Override the configuration of the port
    Config config = ConfigFactory.parseString(
        "akka.remote.netty.tcp.port=" + nodeAddress.port + "\n")
        //+            "akka.remote.artery.canonical.port=" + nodeAddres.port)
        .withFallback(ConfigFactory.load());

    // Create an Akka system
    ActorSystem system = ActorSystem.create("ClusterSystem", config);

    // Create an actor that handles cluster domain events
    return system.actorOf(Props.create(NodeMain.class), "clusterListener");
  }

  private static void checkArgs(String[] args) {
    try {
      for (int i = 0; i < args.length; i++) {
        if (args[i].equalsIgnoreCase("-h")) {
          System.out.println(HELP);
          System.exit(0);
        } else if (command == null) {
          if (args[i].equalsIgnoreCase("start")) {
            command = Command.START;
          } else if (args[i].equalsIgnoreCase("stop")) {
            command = Command.STOP;
          } else if (args[i].equalsIgnoreCase("join")) {
            command = Command.JOIN;
          } else if (args[i].equalsIgnoreCase("leave")) {
            command = Command.LEAVE;
          } else if (args[i].equalsIgnoreCase("mine-facts")) {
            command = Command.MINE_FACTS;
          } else if (args[i].equalsIgnoreCase("get-chain")) {
            command = Command.GET_CHAIN;
          } else if (args[i].equalsIgnoreCase("update-chain")) {
            command = Command.UPDATE_CHAIN;
          } else if (args[i].equalsIgnoreCase("status")) {
            command = Command.STATUS;
          } else {
            new RuntimeException("unknown command " + args[i]);
          }
        } else if (command == Command.START || command == Command.START) {
          if (nodeAddress == null) {
            nodeAddress = new NodeAddress(args[i]);
          } else if (peerAddress == null) {
            peerAddress = new NodeAddress(args[i]);
          } else {
            throw new RuntimeException("too many arguments");
          }
        } else if (command == Command.STOP || command == Command.LEAVE
            || command == Command.GET_CHAIN || command == Command.UPDATE_CHAIN
            || command == Command.STATUS) {
          if (nodeAddress == null) {
            nodeAddress = new NodeAddress(args[i]);
          } else {
            throw new RuntimeException("too many arguments");
          }
        } else if (command == Command.LEAVE) {
          if (nodeAddress == null) {
            nodeAddress = new NodeAddress(args[i]);
          } else {
            throw new RuntimeException("too many arguments");
          }
        } else if (command == Command.MINE_FACTS) {
          if (nodeAddress == null) {
            nodeAddress = new NodeAddress(args[i]);
          } else if (facts == null) {
            facts = args[i];
          } else {
            facts += " " + args[i];
          }
        } else {
          throw new RuntimeException("too many arguments");
        }
      }
      if (command == null) {
        throw new RuntimeException("expected command");
      }

      // defaults
      nodeAddress = (nodeAddress == null ? new NodeAddress() : nodeAddress);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage() + ".");
      System.out.println("Try -h for help.");
      System.exit(-1);
    }
  }

  public static void main(String[] args) {
    checkArgs(args);
    //    System.out.println("COMMAND: " + command);
    //    System.out.println("NODE: " + nodeAddress.toString());
    //    System.out.println("PEER: " + (peerAddress != null ? peerAddress.toString() : "<unknown>"));
    //    //System.exit(-1);

    // process rest of the command
    switch (command) {
      case START: {
        // Override the configuration of the port
        Config config = ConfigFactory
            .parseString("akka.remote.netty.tcp.port=" + nodeAddress.port + "\n")
            //+            "akka.remote.artery.canonical.port=" + nodeAddres.port)
            .withFallback(ConfigFactory.load());
        // Create an Akka system
        ActorSystem system = ActorSystem.create("ClusterSystem", config);
        String nodeName = "node@" + nodeAddress.toString();
        ActorRef node = null;
        node = system.actorOf(Props.create(NodeMain.class), nodeName);
        if (peerAddress != null) {
          node.tell(new NodeMessages.Join(nodeAddress.host, nodeAddress.port), node);
        }
        break;
      }
      case STOP: {
        Config config = ConfigFactory
            .parseString("akka.remote.netty.tcp.port=" + nodeAddress.port + "\n")
            //+            "akka.remote.artery.canonical.port=" + nodeAddres.port)
            .withFallback(ConfigFactory.load());
        ActorSystem system = ActorSystem.create("ClusterSystem", config);
        String nodeName = "node@" + nodeAddress.toString();
        ActorRef node = system.actorFor(nodeName);
        system.stop(node);
        break;
      }
      case JOIN:
        break;
      case LEAVE:
        break;
      case MINE_FACTS:
        break;
      case GET_CHAIN:
        break;
      case UPDATE_CHAIN:
        break;
      case STATUS:
        break;
    }

    /*
    boolean connectToSeed = true;
    System.out.println("Listening on port " + port + ".");
    ActorRef node = startup();
    if (connectToSeed) {
      System.out.println("Connecting to cluster at " + peerHost + ":" + peerPort + "...");
      node.tell(new NodeMessages.Join(peerHost, peerPort), node);
    }
    */
  }
}
