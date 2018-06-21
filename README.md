# blockchain_workshop
Workshop describing the functionality of a blockchain (mining, consensus, conflict resolution, ...).

```
                                  pending to mine
         +---------------------------------------------------------------+
         |                                                               |
         |                      +------------------+                     |
         |                      |      Block       |                     | *
         |                      +------------------+            +--------v-------+
+--------+---------+            | id:String        |            |      Fact      |
| Miner            |   mine     | nonce:String     |  made of * +----------------+
+------------------+ - - - - - -> facts:List<Fact> +------------> dat:ByteBuffer |
| sig:ByteBuffer   |            | prev:String      |            | sig:ByteBuffer |
+--------+---------+            | hash:String      |            +----------------+
         |                      | sig:ByteBuffer   |
         |                      +--------^---------+
         |                               | *
         |                     +---------+----------+
         |                  1  | Chain              |
         +--------------------->--------------------+
                               | blocks:List<Block> |
                               +--------------------+
```