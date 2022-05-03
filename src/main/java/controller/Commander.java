package controller;

import controller.absCommand.Data;
import controller.absCommand.command.*;
import controller.absCommand.Developer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Commander {
    public void useCommander(){
        Developer developer = new Developer(new Exit(),new Save(),new Help(),new History(), new Info(), new Show(), new Insert(), new Clear(),new Script(), new NumOfWheels(), new PrintAscending(), new MinById(),new Update(), new RemoveKey(),new RemoveMin(),new RemoveMax());
        developer.loadMap();
        Data dataIns=Data.getInstance();
        dataIns.setScanner(new Scanner(System.in));
        ClientUDP clientUDP=ClientUDP.getInstance();
        ScriptDecoder scriptDecoder =ScriptDecoder.getInstance();
        scriptDecoder.setScriptName();
        while(true) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    try {
                        System.out.println("Введите команду");
                        String ss = reader.readLine();
                        if ( ss.split(" ")[0].equals("exit")&&ss.split(" ").length==1) {
                            System.out.println("Отключение от сервера. Завершение работы");
                            reader.close();
                            System.exit(0);
                        }
                        Data data=new Data();
                        data.setIntCommand(developer.getMap().get(ss.split(" ")[0]));
                        if(ss.split(" ").length>1) data.setSecondWord(ss.split(" ")[1]);
                        if(ss.split(" ")[0].equals("execute_script")){
                            if(scriptDecoder.isScriptStart()){data.setScriptBuffer();}
                            Data data1=new ScriptDecoder().readScript(data);
                            if(data1.getScriptBuffer().size()!=0) {clientUDP.clientConnection(data1);
                                System.out.println(data1.getScriptBuffer());}
                        }else if(ss.split(" ")[0].equals("insert")|ss.split(" ")[0].equals("update"))clientUDP.clientConnection(new ElementPacket().createPacket(data));
                        else if(developer.getMap().get(ss.split(" ")[0]) != null) clientUDP.clientConnection(data);
                        else System.out.println("Введена неизвестная команда, введите help для справки.");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        System.out.println("Введен EoF(символ конца ввода).");
                        reader = new BufferedReader(new InputStreamReader(System.in));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                System.out.println("Введена пустая строка");
            }
        }
    }

    public void sendCommand(String ss){

    }



}
