import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;

class Player extends Thread
{
    public Player(Socket sock, boolean isFirst) throws Exception
    {
        isFirstPlayer = isFirst;
        socket = sock;
        sysOut = new PrintWriter(System.out, true);
        sockOut = new PrintWriter(socket.getOutputStream(), true);
        sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run()
    {
        try
        {
            function();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void function() throws Exception
    {
        sockOut.println("You were accepted to game");

        if (isFirstPlayer)
        {
            sockOut.println("You were first, write \"start\" to start the game");
        }
        else
        {
            sockOut.println("Wait until the game has started");
        }

        // first player has to start the game
        synchronized (this)
        {
            while (!hasGameStarted && !hasGameEnded)
            {
                if (isFirstPlayer)
                {
                    if (sockIn.ready())
                    {
                        if (howManyPlayers < 2)
                        {
                            sockIn.readLine();
                        }
                        else
                        {
                            // starting game
                            if (sockIn.readLine().equals("start"))
                            {
                                // randomly choosing questions
                                Random random = new Random();
                                int index;

                                for (int i = 0; i < 10; i++)
                                {
                                    index = random.nextInt(20 - i);

                                    questions.remove(index);
                                    answers.remove(index);
                                }

                                // initialising once
                                sysOut.println("Game started");
                                hasGameStarted = true;
                                question = 0;
                                isItPossToAnswer = true;
                                hasSomeoneAnswered = false;
                                hasTimeGoneUp = false;
                                questionStart = System.nanoTime();
                            }
                        }
                    }
                }
            }
        }

        // initialising for each player
        points = 0;
        howManyBadAnswers = 0;
        tooManyBadAnswers = false;
        conGameForPlayer = true;

        // one player playing
        synchronized (this)
        {
            while (conGameForPlayer)
            {
                // initialising for each player for every question
                message = "";
                isThereAnswer = true;
                answer = "yes";
                hasPlayerAnswered = false;
                hasPlayerAnsweredCorr=false;

                // to detect connection loss
                try
                {
                    socket.getOutputStream().write(0);
                }
                catch (Exception e)
                {
                    break;
                }

                // to prevent cheating
                while (sockIn.ready())
                {
                    sockIn.readLine();
                }

                // printing question
                if (tooManyBadAnswers)
                {
                    sockOut.println("Too many bad answers, you skip this question");
                }
                else
                {
                    sockOut.println("- Question " + (question + 1));
                    sockOut.println("- " + questions.get(question));
                }

                // one question
                while (isItPossToAnswer)
                {
                    synchronized (this)
                    {
                        // if the time has gone up
                        if (isFirstPlayer)
                        {
                            if ((System.nanoTime() - questionStart) / 1000000 > 5000)
                            {
                                isItPossToAnswer = false;
                                hasTimeGoneUp = true;
                            }
                        }

                        // if player isn't skipping the question
                        if (!tooManyBadAnswers && !hasPlayerAnswered)
                        {
                            // if player has answered
                            if (sockIn.ready())
                            {
                                while (sockIn.ready())
                                {
                                    message = sockIn.readLine();
                                }

                                // answer
                                switch (message)
                                {
                                    case "y":
                                        hasPlayerAnswered=true;

                                        if (answers.get(question))
                                        {
                                            sockOut.println("Good answer (+1)");
                                            points += 1;
                                            howManyBadAnswers = 0;

                                            isItPossToAnswer = false;
                                            hasSomeoneAnswered = true;
                                            hasPlayerAnsweredCorr=true;
                                        }
                                        else
                                        {
                                            sockOut.println("Bad answer (-2)");
                                            points -= 2;
                                            howManyBadAnswers++;
                                        }
                                        break;
                                    case "n":
                                        hasPlayerAnswered=true;

                                        if (!answers.get(question))
                                        {
                                            sockOut.println("Good answer (+1)");
                                            points += 1;
                                            howManyBadAnswers = 0;

                                            isItPossToAnswer = false;
                                            hasSomeoneAnswered = true;
                                            hasPlayerAnsweredCorr=true;
                                        }
                                        else
                                        {
                                            sockOut.println("Bad answer (-2)");
                                            points -= 2;
                                            howManyBadAnswers++;
                                        }
                                        break;
                                    default:
                                        sockOut.println("ERROR");
                                        break;
                                }
                            }
                        }
                    }
                }

                // 'tooManyBadAnswers' lasts only one iteration
                if (tooManyBadAnswers)
                {
                    tooManyBadAnswers = false;
                }

                if (howManyBadAnswers >= 3)
                {
                    howManyBadAnswers = 0;
                    tooManyBadAnswers = true;
                }

                // if someone has answered in time
                if (hasSomeoneAnswered)
                {
                    if (hasPlayerAnswered && hasPlayerAnsweredCorr)
                    {
                        // next question
                        if (question < 9)
                        {
                            question++;
                            questionStart = System.nanoTime();
                            isItPossToAnswer = true;
                            hasSomeoneAnswered = false;
                            hasTimeGoneUp = false;
                        }
                        else
                        {
                            hasGameEnded = true;
                        }
                    }
                    else
                    {
                        sockOut.println("Someone has already answered the question");
                    }
                }

                // if no one answered in time
                if (hasTimeGoneUp)
                {
                    sockOut.println("Time to answer the question has ended");

                    if (isFirstPlayer)
                    {
                        // next question
                        if (question < 9)
                        {
                            question++;
                            questionStart = System.nanoTime();
                            isItPossToAnswer = true;
                            hasSomeoneAnswered = false;
                            hasTimeGoneUp = false;
                        }
                        else
                        {
                            hasGameEnded = true;
                        }
                    }
                }

                // ending game
                if (hasGameEnded)
                {
                    sockOut.println("Game ended, your score: " + points);
                    conGameForPlayer = false;

                    if (isFirstPlayer)
                    {
                        sysOut.println("Game ended");
                    }
                }
            }

            sockOut.println("You were removed from game");
            sysOut.println("Disconnected with a player");
            howManyPlayers--;
        }
    }

    Socket socket;
    PrintWriter sysOut;
    PrintWriter sockOut;
    BufferedReader sockIn;
    String message;
    boolean isFirstPlayer;
    boolean conGameForPlayer;
    String answer;
    boolean isThereAnswer;
    boolean hasPlayerAnswered;
    boolean hasPlayerAnsweredCorr;
    int points;
    int howManyBadAnswers;
    boolean tooManyBadAnswers;

    static int howManyPlayers;
    static boolean hasGameStarted;
    static boolean hasGameEnded;
    static ArrayList<String> questions;
    static ArrayList<Boolean> answers;
    static int question;
    static long questionStart;
    static boolean isItPossToAnswer;
    static boolean hasSomeoneAnswered;
    static boolean hasTimeGoneUp;
}
