java -ea -cp lib/ants-api.jar:lib/ants-server.jar:ants-impl/build/libs/ants-impl.jar:target/amaitjo-1.0.0-jar-with-dependencies.jar org/linkedin/contest/ants/server/AntServer -B -p1 amaitjo.brains.bt.AntyGaga -p2 amaitjo.brains.bt.Ozzy -r gamelog/session.txt

#java -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=y -ea -cp lib/ants-api.jar:lib/ants-server.jar:ants-impl/build/libs/ants-impl.jar org/linkedin/contest/ants/server/AntServer -B -p1 org.linkedin.contest.ants.impl.RadialAnt -p2 org.linkedin.contest.ants.impl.RadialAnt
