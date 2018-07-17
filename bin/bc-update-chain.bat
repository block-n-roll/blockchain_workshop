@echo on
set CP=%~dp0\..\lib\*
java -cp %CP% org.blocknroll.blockchain.workshop.NodeMain update-chain %*
