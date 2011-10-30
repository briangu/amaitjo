mkdir gameplay
mv gameplay/report.out gameplay/report.out.bak

java -ea -Xmx2048m -cp lib/ants-api.jar:lib/ants-server.jar:ants-impl/build/libs/ants-impl.jar:target/amaitjo-1.0.0-jar-with-dependencies.jar org/linkedin/contest/ants/server/AntServer -B -p1 amaitjo.brains.PassAnt -p2 amaitjo.brains.bt.Ozzy -r gameplay/report.out
#java -ea -Xmx2048m -cp lib/ants-api.jar:lib/ants-server.jar:ants-impl/build/libs/ants-impl.jar:target/amaitjo-1.0.0-jar-with-dependencies.jar org/linkedin/contest/ants/server/AntServer -B -p1 amaitjo.brains.bt.EvilOzzy -p2 amaitjo.brains.bt.Ozzy -r gameplay/report.out

grep "^(" gameplay/report.out | grep "Food:" | cut -d " " -f 4 | awk '{total = total + $1}END{print total}'

#java -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=y -ea -cp lib/ants-api.jar:lib/ants-server.jar:ants-impl/build/libs/ants-impl.jar org/linkedin/contest/ants/server/AntServer -B -p1 org.linkedin.contest.ants.impl.RadialAnt -p2 org.linkedin.contest.ants.impl.RadialAnt
