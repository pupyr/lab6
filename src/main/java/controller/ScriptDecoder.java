package controller;

import controller.absCommand.Data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScriptDecoder implements Serializable {

    /**
     * scriptStart- переменная, проверяющая, работает ли скрипт
     * (каждая команда после своего выполнения выводит "команда успешно выполнена",
     * чтобы во время скрипта эта строка не выводилась, и нужна эта переменная)
     * recursion- переменная, сигнализирующая о рекурсии
     * inner- переменная, сигнализирующая о вложенности
     * scriptName- массив, хранящий названия скриптов, которые вызывались по ходу выполнения команды
     * (требуется для проверки рекурсии)
     * i- переменная, показывающая уровни вложенности
     */
    private static ScriptDecoder Instance;

    public static ScriptDecoder getInstance() {
        if (Instance == null) {
            Instance = new ScriptDecoder();
        }
        return Instance;
    }

    private boolean correctScript=true;
    private boolean scriptStart =false;
    private boolean recursion=false;
    private boolean inner=false;
    private List<String> scriptName=new ArrayList<>();
    private int i;

    int I;

    public boolean isCorrectScript() {
        return correctScript;
    }

    public void setCorrectScript(boolean correctScript) {
        this.correctScript = correctScript;
    }

    public boolean isRecursion() {
        return !recursion;
    }

    public void setRecursion(boolean recursion) {
        this.recursion = recursion;
    }

    public boolean isInner() {
        return inner;
    }

    public void setInner(boolean inner) {
        this.inner = inner;
    }

    public List<String> getScriptName() {
        return scriptName;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public void setScriptName() {
        scriptName = new ArrayList<>();
    }

    public boolean isScriptStart() {
        return !scriptStart;
    }

    public void setScriptStart(boolean scriptStart) {
        this.scriptStart=scriptStart;
    }

    public Data readScript(Data data) {
        ScriptDecoder scriptDecoder =getInstance();
        Data dataInstance=Data.getInstance();
        scriptDecoder.getScriptName().add(data.getSecondWord());
        if(!scriptDecoder.isScriptStart())scriptDecoder.setI(1);
        if(!scriptDecoder.isScriptStart())data.setScriptBuffer();
        try (Scanner scanner = new Scanner(new FileReader("src/main/resources/"+data.getSecondWord()))) {
            dataInstance.setScanner(scanner);
            scriptDecoder.setScriptStart(false);
            while (scanner.hasNext() && scriptDecoder.isRecursion()) {
                I=5;
                String ss = scanner.nextLine();
                if(ss.split(" ")[0].equals("execute_script")) {
                    scriptDecoder.setI(scriptDecoder.getI() + 1);
                    scriptDecoder.setInner(true);
                    if(getInstance().isRecursion()&&ss.split(" ").length>1)checkRecursion(ss.split(" ")[1],data);
                    Data data1=readScript(new Data(ss.split(" ")[1]));
                    if(data1.getScriptBuffer()!=null&&data.getScriptBuffer()!=null)data.getScriptBuffer().addAll(data1.getScriptBuffer());
                }
                if (scriptDecoder.isRecursion()&&!ss.split(" ")[0].equals("execute_script")) {
                    if(data.getScriptBuffer()!=null){
                        data.getScriptBuffer().add(ss);
                    }
                }
            }
            if(I==0&&data.getScriptBuffer().size()==0) System.out.println("Скрипт пуст");
            checkEndOfScript(data);
        }
        catch (FileNotFoundException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            data.setDataForClient("Введено неверное имя скрипта");
        }
        return data;
    }

    public void checkRecursion(String ss,Data data){
        ScriptDecoder scriptDecoder = getInstance();
        if(scriptDecoder.getScriptName().size() != 0) {
            int i=0;
            for (String q : scriptDecoder.getScriptName()) {
                if (q.equals(ss)) i++;
                if(i>0){
                    System.out.println("В файле есть рекурсия. Выполнены все команды до рекурсии");
                    scriptDecoder.setRecursion(true);
                    scriptDecoder.setI(scriptDecoder.getI() - 1);
                    data.setScriptBuffer();
                    break;
                }
            }
        }
    }

    public void checkEndOfScript(Data data){
        ScriptDecoder scriptDecoder =getInstance();
        if(scriptDecoder.isInner()){
            scriptDecoder.setI(scriptDecoder.getI()-1);
            scriptDecoder.getScriptName().remove(scriptDecoder.getScriptName().size()-1);
        }else{
            if(getInstance().isCorrectScript()&& !getInstance().isRecursion())System.out.println("скрипт завершен");
            else data.setScriptBuffer();
            I=0;
            Data.getInstance().setScanner(new Scanner(System.in));
            getInstance().setCorrectScript(true);
            getInstance().setScriptStart(true);
            getInstance().setScriptName();
        }
        if (scriptDecoder.isInner()&& scriptDecoder.getI()==0){
            getInstance().setScriptStart(true);
            scriptDecoder.setI(1);
            scriptDecoder.setInner(false);
            if(scriptDecoder.isRecursion())System.out.println("скрипт выполнен");
            Data.getInstance().setScanner(new Scanner(System.in));
            getInstance().setCorrectScript(true);
            scriptDecoder.setRecursion(false);
            getInstance().setScriptName();
        }
    }


}
