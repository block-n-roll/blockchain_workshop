package org.blocknroll.blockchain.workshop;

import java.util.Scanner;

public class Main {

  private final static int DEFAULT_PORT = 2551;

  private final static int CMD_SUCCESS = 0;
  private final static int CMD_ERROR = -1;
  private final static int CMD_ABORT = -2;

  private static int port;

  private static void checkArgs(String[] args) {
    switch (args.length) {
      case 0:
        port = DEFAULT_PORT;
        System.out.println("Using default port " + port + ".");
        break;
      case 1:
        port = Integer.parseInt(args[1]);
        System.out.println("Using port " + port + ".");
        break;
      default:
        System.out.println("Error: too many arguments.");
        System.out.println("Expected port number.");
        System.exit(-1);
    }
  }

  private static int joinCmd(Scanner args) {
    System.out.println("To be implemented...");
    return CMD_ERROR;
  }

  private static int helpCmd(Scanner args) {
    if (args.hasNext()) {
      System.out.println("Error: too many arguments.");
      return CMD_ERROR;
    }
    System.out.println("join IP PORT\tJoins to a cluster by contacting to IP:PORT.");
    System.out.println("leave\tLeaves cluster.");
    System.out.println("add FACTS...\tCreates a new block with input facts and starts mining.");
    System.out.println("chain\tDisplays current chain.");
    System.out.println("chain\tDisplays current chain.");
    System.out.println("help\tPrints this help.");
    System.out.println("exit\tExists the command loop and finishes the program.");
    return CMD_SUCCESS;
  }

  private static int exitCmd(Scanner args) {
    if (args.hasNext()) {
      System.out.println("Error: too many arguments.");
      return CMD_ERROR;
    }
    System.out.println("Bye!");
    return CMD_ABORT;
  }

  private static int repl() {
    int result = CMD_SUCCESS;
    while (result != CMD_ABORT) {
      Scanner command = new Scanner(System.in);
      System.out.println(">>");
      switch (command.next().toUpperCase()) {
        case "join":
          result = joinCmd(command);
          break;
        case "help":
          result = helpCmd(command);
          break;
        case "exit":
          result = exitCmd(command);
          break;
        default:
          System.out.println("Error: command not found.");
          result = CMD_ERROR;
          break;
      }
      command.close();
    }
    return result;
  }

  public static void main(String[] args) {
    checkArgs(args);
    System.exit(repl());
  }
}
