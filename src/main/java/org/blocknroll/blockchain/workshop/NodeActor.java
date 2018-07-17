package org.blocknroll.blockchain.workshop;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.management.AkkaManagement;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.net.InetSocketAddress;

public class NodeActor extends AbstractActor {

  // some constants
  private final static String HELP = "\n".join(
      "Usage:",
      "   NodeActor [OPTIONS] [PEER_NODE]",
      "",
      "Options:",
      "   -h\tPrints this help and exists.",
      "   -p PORT\tUse PORT for listening to messages (random port by default).",
      "",
      "Notes:",
      "   A PEER_NODE has the form IP:PORT, assuming localhost when IP is not given.");
  public final static String DEFAULT_HOST = "127.0.0.1";
  public final static int DEFAULT_PORT = 2550;

  // cluster objects
  private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
  private Cluster cluster = Cluster.get(getContext().system());

  // arguments
  public static Integer nodePort = null;
  public static InetSocketAddress peerAddress = null;

  public static InetSocketAddress parseAddress(String address) {
    String host = DEFAULT_HOST;
    int port = DEFAULT_PORT;
    if (address != null && !address.isEmpty()) {
      if (address.contains(":")) {
        String[] pair = address.trim().split(":");
        host = (pair[0].equalsIgnoreCase("localhost") ? DEFAULT_HOST : pair[0]);
        port = Integer.parseInt(pair[1]);
      } else if (address.contains(".")) {
        host = address.trim();
      } else {
        port = Integer.parseInt(address.trim());
      }
    }
    return new InetSocketAddress(host, port);
  }

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

  private static void checkArgs(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equalsIgnoreCase("-h")) {
        System.out.println(HELP);
        System.exit(0);
      } else if (args[i].equalsIgnoreCase("-p")) {
        i++;
        if (i == args.length) {
          throw new RuntimeException("PORT expected for -p option.");
        }
        nodePort = Integer.parseInt(args[i]);
      } else if (peerAddress == null) {
        peerAddress = parseAddress(args[i]);
      } else {
        throw new RuntimeException("too many arguments: " + args[i]);
      }
    }
  }

  public static ActorRef startup(InetSocketAddress address) {
    // Override the configuration of the port
    Config config = ConfigFactory.parseString(
        "akka.remote.netty.tcp.port=" + address.getPort() + "\n")
        //+            "akka.remote.artery.canonical.port=" + nodeAddres.port)
        .withFallback(ConfigFactory.load());

    // Create an Akka system
    ActorSystem system = ActorSystem.create("ClusterSystem", config);

    // Create an actor that handles cluster domain events
    return system.actorOf(Props.create(NodeActor.class), "clusterListener");
  }

  public static void main(String[] args) {
    checkArgs(args);
    ActorSystem system;
    Config config;
    if (nodePort != null) {
      config = ConfigFactory.parseString(
          "akka.remote.netty.tcp.port=" + nodePort + "\n")
          //+            "akka.remote.artery.canonical.port=" + nodePort)
          .withFallback(ConfigFactory.load());
    } else {
      config = ConfigFactory.load();
    }
    system = ActorSystem.create("ClusterSystem", config);
    try {
      AkkaManagement.get(system).start();
    } catch (Exception e) {
      // ignore
    }

    ActorRef node = system.actorOf(Props.create(NodeActor.class), "clusterListener");
    System.out.println("LAMADREQUE: " + node.path());
    if (peerAddress != null) {
      String uri = "akka.tcp://ClusterSystem@" + peerAddress.getHostName() + ":" + peerAddress.getPort() + "/user/clusterListener";
      System.out.println("Connecting to cluster at " + uri + "...");
      ActorSelection selection = system.actorSelection(uri);
      selection.tell(new NodeMessages.Join(peerAddress.getHostName(), peerAddress.getPort()), node);
    }
  }
}
