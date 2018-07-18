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

# blockchain_workshop status
![Build status](https://travis-ci.com/block-n-roll/blockchain_workshop.svg?branch=master)

# Contribution
We follow google code style for this repository.
Please find below the files to configure your IDE:

* [intellij](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)
* [eclipse](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml)