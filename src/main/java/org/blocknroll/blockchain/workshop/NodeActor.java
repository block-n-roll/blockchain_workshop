package org.blocknroll.blockchain.workshop;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.management.AkkaManagement;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class NodeActor extends UntypedActor {

  // some constants
  public final static String DEFAULT_HOST = "127.0.0.1";
  public final static int DEFAULT_PORT = 2550;
  private final static String HELP = "\n".join(
      "Usage:",
      "   NodeActor [OPTIONS] [PEER_NODE]",
      "",
      "Options:",
      "   -h\tPrints this help and exists.",
      "   -h IP\tUse IP as localhost.",
      "   -p PORT\tUse PORT for listening to messages (" + DEFAULT_PORT + ").",
      "",
      "Notes:",
      "   A PEER_NODE has the form IP:PORT, assuming localhost when IP is not given.");

  // cluster objects
  private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
  //private Cluster cluster = Cluster.get(getContext().system());

  // arguments
  public static String nodeHost = null;
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
    /*
    if (peerAddress != null) {
      String address = peerAddress.toString().substring(1);
      String name = buildName(peerAddress.getPort());
      String path = "akka.tcp://ClusterSystem@" + address + "/user/" + name;
      ActorRef actor = getContext().actorFor(path);
      System.out.println(path);
      ActorSelection selection = getContext().system()
          .actorSelection(path); //getContext().actorSelection(path);
      System.out.println(selection.pathString());
      NodeMessages.Join msg = new NodeMessages.Join(nodeHost, nodePort);
      selection.tell(msg, self());
    }
    */
  }

  @Override
  public void onReceive(Object msg) {
    log.info("Processing message...");
    Cluster cluster = Cluster.get(getContext().system());
    if (msg instanceof NodeMessages.Join) {
      /*
      log.info("Joining to " + msg.toString() + "...");
      NodeMessages.Join params = (NodeMessages.Join) msg;
      Address address = new Address("akka.tcp", "ClusterSystem", params.host, params.port);
      cluster.join(address);
      */
    } else if (msg instanceof NodeMessages.Leave) {
      /*
      cluster.leave(cluster.selfAddress());
      log.info("Leaving cluster...");
      Address address = new Address("akka.tcp", "ClusterSystem");
      cluster.join(address);
      */
    } else {
      log.error("Unknown message of type " + msg.getClass());
    }
  }

  public static String getLocalhost() {
    List<String> ips = new LinkedList<String>();
    try {
      Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
      while (n.hasMoreElements()) {
        NetworkInterface e = n.nextElement();
        Enumeration<InetAddress> a = e.getInetAddresses();
        while (a.hasMoreElements()) {
          InetAddress addr = a.nextElement();
          if (addr instanceof Inet4Address && addr.isReachable(10000) && !addr
              .isLoopbackAddress()) {
            ips.add(addr.getHostAddress());
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (ips.size() == 0) {
      throw new RuntimeException("No reachable IP found.");
    } else if (ips.size() > 1) {
      System.out.println("WARNING: Too many localhost IPs found: " + ips + ". Use -i option.");
    }
    return ips.get(0);
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
      } else if (args[i].equalsIgnoreCase("-i")) {
        i++;
        if (i == args.length) {
          throw new RuntimeException("IP expected for -i option.");
        }
        nodeHost = args[i];
      } else if (peerAddress == null) {
        peerAddress = parseAddress(args[i]);
      } else {
        throw new RuntimeException("too many arguments: " + args[i]);
      }
    }
    if (nodePort == null) {
      nodePort = DEFAULT_PORT;
    }
    if (nodeHost == null) {
      nodeHost = getLocalhost();
    }
  }

  public static String buildName(int port) {
    return "node@" + port;
  }

  public static void main(String[] args) throws Exception {
    // initialize
    checkArgs(args);

// build configuration
    System.out.println("LOCALHOST: " + nodeHost);
    String seedAddress = "";
    if (peerAddress != null) {
      seedAddress = peerAddress.getHostString() + ":" + peerAddress.getPort();
    } else {
      seedAddress = nodeHost + ":" + nodePort;
    }
    Properties ps = new Properties();
    ps.setProperty("akka.loglevel", "INFO");
//ps.setProperty("akka.actor.provider", "akka.remote.RemoteActorRefProvider");
    ps.setProperty("akka.actor.provider", "cluster");
//ps.setProperty("akka.remote.enabled-transports.0", "akka.remote.netty.tcp");
    ps.setProperty("akka.cluster.seed-nodes.0", "akka.tcp://ClusterSystem@" + seedAddress);
    ps.setProperty("akka.remote.transport", "akka.remote.netty.NettyRemoteTransport");
    ps.setProperty("akka.remote.netty.tcp.hostname", nodeHost);
    ps.setProperty("akka.remote.netty.tcp.port", nodePort.toString());
    ps.setProperty("akka.remote.log-sent-messages", "on");
    ps.setProperty("akka.remote.log-received-messages", "on");
    Config config = ConfigFactory.parseProperties(ps);
    System.out.println(config);


    // create system & actor
    ActorSystem system = ActorSystem.create("ClusterSystem", config);
    ActorRef node = system.actorOf(Props.create(NodeActor.class), buildName(nodePort));
    AkkaManagement.get(system).start();
  }
}
