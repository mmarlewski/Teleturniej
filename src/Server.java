import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{
    public static void main(String[] args) throws Exception
    {
        sysOut = new PrintWriter(System.out, true);
        serverSock = new ServerSocket(8888);

        sysOut.println("Created server");

        Player.hasGameStarted = false;
        Player.hasGameEnded = false;
        Player.howManyPlayers = 0;

        Player.questions = new ArrayList<>();
        Player.questions.add("Did the Western Roman Empire fall in 476 CE?");
        Player.questions.add("Did Julius Caesar die in 44 BCE?");
        Player.questions.add("Was Rome built on 7 hills?");
        Player.questions.add("Was Julius Caesar the emperor during the first year of the Pax Romana?");
        Player.questions.add("Did the Byzantine Empire fall to the Ottoman Empire in 1453 CE?");
        Player.questions.add("Was Battle of Cannae fought with Carthage?");
        Player.questions.add("Were there 2 punic wars?");
        Player.questions.add("Was Octavian in the Second Triumvirate?");
        Player.questions.add("Did the consuls command the army?");
        Player.questions.add("Did Virgil wrote the Aeneid?");
        Player.questions.add("Did Pompeius win war with Caesar?");
        Player.questions.add("Was Caesar titled a dictator for life?");
        Player.questions.add("Was Germania annexed into the Roman Empire?");
        Player.questions.add("Were there 5 consuls in the Roman Republic?");
        Player.questions.add("Was Oktavian the first emperor?");
        Player.questions.add("Did the Roman Senate exist in thr Roman Kingdom?");
        Player.questions.add("Was London founded by the Romans?");
        Player.questions.add("Did Nero started the Great Fire in Rome?");
        Player.questions.add("Did the Romans destroy the First Temple of Jerusalem?");
        Player.questions.add("Was roman original religion monotheistic?");

        Player.answers = new ArrayList<>();
        Player.answers.add(true);
        Player.answers.add(true);
        Player.answers.add(true);
        Player.answers.add(false);
        Player.answers.add(true);
        Player.answers.add(true);
        Player.answers.add(false);
        Player.answers.add(true);
        Player.answers.add(true);
        Player.answers.add(true);
        Player.answers.add(false);
        Player.answers.add(true);
        Player.answers.add(false);
        Player.answers.add(false);
        Player.answers.add(true);
        Player.answers.add(true);
        Player.answers.add(true);
        Player.answers.add(false);
        Player.answers.add(false);
        Player.answers.add(false);

        while (Player.howManyPlayers < 4)
        {
            playerSock = serverSock.accept();

            if (Player.hasGameStarted)
            {
                break;
            }

            if (Player.howManyPlayers == 0)
            {
                new Player(playerSock, true).start();
                sysOut.println("Connected with first player");
            }
            else
            {
                new Player(playerSock, false).start();
                sysOut.println("Connected with a player");
            }

            Player.howManyPlayers++;
        }
    }

    static ServerSocket serverSock;
    static Socket playerSock;
    static PrintWriter sysOut;
}
