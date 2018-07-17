package org.blocknroll.blockchain.workshop;

import static org.blocknroll.blockchain.workshop.NodeActor.parseAddress;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.Serializable;
import java.net.InetSocketAddress;

public class NodeMain extends AbstractActor {

  // some constants
  private final static String HELP = "\n".join(
      "Usage:",
      "   COMMAND [OPTIONS] [ARGUMENTS]",
      "",
      "Commands:",
      "   start [PORT [PEER_NODE]]",
      "      Starts a local node listening to PORT for commands. Optionally it joins to PEER_NODE.",
      "   stop [NODE]",
      "      Stops a local or remote NODE (use with care!).",
      "   join [NODE [PEER_NODE]]",
      "      Makes NODE to leave current cluster and to join to PEER_NODE.",
      "   leave [NODE]",
      "      Makes NODE to leave its current cluster but keeps it running.",
      "   mine-facts [NODE [FACTS...]]",
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

  // cluster objects
  private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
  private Cluster cluster = Cluster.get(getContext().system());

  // input arguments
  private static class Arguments implements Serializable {

    public Command command = null;
    public InetSocketAddress nodeAddress = null;
    public InetSocketAddress peerAddress = null;
    public String facts = null;
  }

  private static String nodeName(String prefix, InetSocketAddress address) {
    return prefix + "@" + address.getHostName() + ":" + address.getPort();
  }

  public NodeMain(Arguments args)
  {
    System.out.println("Aquí estamos");
    self().tell(args, self());
  }

  @Override
  public void preStart() {
    cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(), MemberEvent.class,
        UnreachableMember.class);
  }

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
        .match(Arguments.class, msg -> {
          // cluster.join(msg.nodeAddress);
          String nodeName = nodeName("node", msg.nodeAddress);
          switch (msg.command) {
            case START:
              ActorRef node = getContext().actorOf(Props.create(NodeActor.class));
              //ActorRef node = NodeActor.startup(msg.nodeAddress);
              if (msg.peerAddress != null) {
                node.tell(
                    new NodeMessages.Join(msg.peerAddress.getHostName(), msg.peerAddress.getPort()),
                    getSelf());
              }
              break;

            case STOP: {
              ActorSelection selection = getContext().actorSelection(nodeName);
              selection.tell(PoisonPill.getInstance(), getSelf());
              break;
            }
          }
        })
        .build();
  }

  private static Arguments checkArgs(String[] args) {
    Arguments result = new Arguments();

    try {
      for (int i = 0; i < args.length; i++) {
        if (args[i].equalsIgnoreCase("-h")) {
          System.out.println(HELP);
          System.exit(0);
        } else if (result.command == null) {
          if (args[i].equalsIgnoreCase("start")) {
            result.command = Command.START;
          } else if (args[i].equalsIgnoreCase("stop")) {
            result.command = Command.STOP;
          } else if (args[i].equalsIgnoreCase("join")) {
            result.command = Command.JOIN;
          } else if (args[i].equalsIgnoreCase("leave")) {
            result.command = Command.LEAVE;
          } else if (args[i].equalsIgnoreCase("mine-facts")) {
            result.command = Command.MINE_FACTS;
          } else if (args[i].equalsIgnoreCase("get-chain")) {
            result.command = Command.GET_CHAIN;
          } else if (args[i].equalsIgnoreCase("update-chain")) {
            result.command = Command.UPDATE_CHAIN;
          } else if (args[i].equalsIgnoreCase("status")) {
            result.command = Command.STATUS;
          } else {
            new RuntimeException("unknown command " + args[i]);
          }
        } else if (result.command == Command.START || result.command == Command.JOIN) {
          if (result.nodeAddress == null) {
            result.nodeAddress = parseAddress(args[i]);
          } else if (result.peerAddress == null) {
            result.peerAddress = parseAddress(args[i]);
          } else {
            throw new RuntimeException("too many arguments");
          }
        } else if (result.command == Command.STOP || result.command == Command.LEAVE
            || result.command == Command.GET_CHAIN || result.command == Command.UPDATE_CHAIN
            || result.command == Command.STATUS) {
          if (result.nodeAddress == null) {
            result.nodeAddress = parseAddress(args[i]);
          } else {
            throw new RuntimeException("too many arguments");
          }
        } else if (result.command == Command.LEAVE) {
          if (result.nodeAddress == null) {
            result.nodeAddress = parseAddress(args[i]);
          } else {
            throw new RuntimeException("too many arguments");
          }
        } else if (result.command == Command.MINE_FACTS) {
          if (result.nodeAddress == null) {
            result.nodeAddress = parseAddress(args[i]);
          } else if (result.facts == null) {
            result.facts = args[i];
          } else {
            result.facts += " " + args[i];
          }
        } else {
          throw new RuntimeException("too many arguments");
        }
      }
      if (result.command == null) {
        throw new RuntimeException("expected command");
      }

      // defaults
      result.nodeAddress = (result.nodeAddress == null ? parseAddress(null) : result.nodeAddress);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage() + ".");
      System.out.println("Try -h for help.");
      System.exit(-1);
    }
    return result;
  }

  public static void main(String[] args) {
    Arguments arguments = checkArgs(args);
    //    System.out.println("COMMAND: " + command);
    //    System.out.println("NODE: " + nodeAddress.toString());
    //    System.out.println("PEER: " + (peerAddress != null ? peerAddress.toString() : "<unknown>"));
    //    //System.exit(-1);

    // create this actor and join to our node
    //InetSocketAddress mainAddress = new InetSocketAddress("127.0.0.1", 12345);
    InetSocketAddress mainAddress = parseAddress(null);
    /*
    Config config = ConfigFactory
        .parseString("akka.remote.netty.tcp.port=" + mainAddress.getPort() + "\n")
        //+            "akka.remote.artery.canonical.port=" + mainAddres.getPort())
        .withFallback(ConfigFactory.load());*/
    ActorSystem system = ActorSystem.create("ClusterSystem");
    String mainName = nodeName("main", mainAddress);
    ActorRef mainNode = system.actorOf(Props.create(NodeMain.class, arguments), mainName);
    //mainNode.tell(arguments, mainNode);

    /*
    switch (arguments.command) {

      case START:
        break;
      }

      case STOP: {
        String nodeName = "node@" + nodeAddress.toString();
        ActorSelection selection = context.actorSelection(nodeName);
        selection.tell(new PoisonPill(), );
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

    / *
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
