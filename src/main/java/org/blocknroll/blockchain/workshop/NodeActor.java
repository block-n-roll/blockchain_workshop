package org.blocknroll.blockchain.workshop;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe;
import akka.management.AkkaManagement;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.blocknroll.blockchain.workshop.Message.MineFacts;
import org.blocknroll.blockchain.workshop.Message.ProofOfWorkResponse;
import org.blocknroll.blockchain.workshop.Message.RequestProofOfWork;

public class NodeActor extends AbstractActor implements Cluster {

  // some constants
  public final static String DEFAULT_HOST = "127.0.0.1";
  public final static int DEFAULT_PORT = 2550;
  private final static String HELP = String.join(
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
  private static final Logger logger = LogManager.getLogger(NodeActor.class);
  private static final float POW_THRESHOLD = 51.0f;
  // arguments
  public static String nodeHost = null;
  public static Integer nodePort = null;
  public static InetSocketAddress peerAddress = null;
  private akka.cluster.Cluster cluster = akka.cluster.Cluster.get(getContext().system());
  private NodeImp node;

  private Map<Long, Long> pow = new HashMap<>();

  public NodeActor() throws Exception {
    // Subscribe to events
    ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
    mediator.tell(new Subscribe("MineFacts", getSelf()), getSelf());
    mediator.tell(new Subscribe("RequestProofOfWork", getSelf()), getSelf());
    mediator.tell(new Subscribe("ProofOfWorkResponse", getSelf()), getSelf());
    mediator.tell(new Subscribe("BlockMined", getSelf()), getSelf());
    node = new NodeImp(this);
  }

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
    if (nodePort == DEFAULT_PORT) {
      AkkaManagement.get(system).start();
    }
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
        .match(MineFacts.class, req -> {
          logger.info("Mining facts ...");
          node.mineFacts(req.facts);
        })
        .match(RequestProofOfWork.class, req -> {
          logger.info("Proof of work requested for block " + req.block.getIdentifier());
          if (!pow.containsKey(req.block.getIdentifier())) {
            new Message.ProofOfWorkResponse(req.block,
                node.verifyBlock(req.block, node.getLastBlock()));
          } else {
            logger.warn("Discarding block " + req.block.getIdentifier() + ". Already computed.");
          }
        })
        .match(ProofOfWorkResponse.class, res -> {
          logger.info(
              "Proof of work response for block " + res.block.getIdentifier() + " is " + res.block
                  .getIdentifier());
          if (node.getLastBlock().getIdentifier() < res.block.getIdentifier()) {
            pow.compute(res.block.getIdentifier(), (k, v) -> (v != null) ? v + 1 : 1);
            int count = cluster.state().members().filter(s -> (s.status().equals(MemberStatus.Up)))
                .size();
            if ((100.0f * pow.get(res.block.getIdentifier()) / count) > POW_THRESHOLD) {
              node.processBlock(Collections.singletonList(res.block));
            }
          } else {
            logger.warn("Discarding proof of work response for block " + res.block.getIdentifier()
                + ". Already processed block with the same identifier.");
          }
        })
        .build();
  }

//  @Override
//  public void onReceive(Object msg) {
//    log.info("Processing message...");
//    Cluster cluster = Cluster.get(getContext().system());
//    if (msg instanceof Message.Join) {
//      /*
//      log.info("Joining to " + msg.toString() + "...");
//      Message.Join params = (Message.Join) msg;
//      Address address = new Address("akka.tcp", "ClusterSystem", params.host, params.port);
//      cluster.join(address);
//      */
//    } else if (msg instanceof Message.Leave) {
//      /*
//      cluster.leave(cluster.selfAddress());
//      log.info("Leaving cluster...");
//      Address address = new Address("akka.tcp", "ClusterSystem");
//      cluster.join(address);
//      */
//    } else {
//      log.error("Unknown message of type " + msg.getClass());
//    }
//  }

  @Override
  public void setSeed(Cluster seed) {
    logger.warn("Ignoring seed... automatic cluster configuration enabled.");
  }

  @Override
  public void requestProofOfWork(Block block) throws Exception {
    DistributedPubSub.get(getContext().system()).mediator()
        .tell(new RequestProofOfWork(block), getSelf());
  }

  @Override
  public String getId() {
    return nodeHost + "_" + nodePort;
  }

  @Override
  public void addPeer(Cluster n) {
    logger.warn("Ignoring add peer... automatic cluster configuration enabled.");
  }

  @Override
  public List<Cluster> getPeers() {
    logger.warn("Ignoring get peers... automatic cluster configuration enabled.");
    return null;
  }

  @Override
  public void addFacts(Collection<Fact> facts) throws Exception {
    node.mineFacts(facts);
  }

  @Override
  public Chain getChain() {
    return node.getChain();
  }

  @Override
  public Block getLastBlock() {
    return node.getLastBlock();
  }

  @Override
  public void processBlocks(List<Block> block) throws Exception {
    node.processBlock(block);
  }

  @Override
  public void requestChain() throws Exception {

  }
}
