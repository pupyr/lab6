package controller;

import controller.absCommand.Data;
import model.*;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ElementPacket {

        Vehicle vehicle;
        Long Lmax;

    public ElementPacket() {
        vehicle = new Vehicle();
        Lmax = 0L;
    }

    public void handle(Scanner scan,Predicate<String> pr, Consumer<String> cn,String err,Predicate<String> pr2, Runnable rn){
        String s1=scan.nextLine();
        if(pr2.test(s1)) {
            if (pr.test(s1)) cn.accept(s1);
            else {
                System.out.println(err);
                handle(scan, pr, cn, err,pr2,rn);
            }
        }else rn.run();
    }

    public void setId () {
        Root root = Root.getInstance();
        for (Integer l : root.getRoot().keySet()) {
            if (Lmax < root.getRoot().get(l).getId()) {
                Lmax = root.getRoot().get(l).getId();
            }
        }
        vehicle.setId(Lmax + 1L);
    }
    public void setName (Scanner scanner){
        if(ScriptDecoder.getInstance().isScriptStart())System.out.println("Введите имя");
        handle(scanner,s->!s.equals(""),s->vehicle.setName(s),"Поле обязательно для ввода",s->true,System.out::println);
    }
    public void setCoordinates (Scanner scanner) {
        if (ScriptDecoder.getInstance().isScriptStart()) System.out.println("Введите координаты");
        Coordinates coordinates = new Coordinates();
        while (true) {
            try {
                if (ScriptDecoder.getInstance().isScriptStart()) System.out.println("Введите x");

                handle(scanner, s -> Float.parseFloat(s) < 286.0f,
                        s -> coordinates.setX(Float.parseFloat(s)),
                        "х должно быть меньше 286",
                        s->true, System.out::println);

                if (ScriptDecoder.getInstance().isScriptStart()) System.out.println("Введите y");

                handle(scanner, s -> Double.parseDouble(s) < 53.0,
                        s -> coordinates.setY(Double.parseDouble(s)),
                        "у должно быть меньше 53",
                        s->true,System.out::println);

                break;
            } catch (NumberFormatException e) {
                System.out.println("поле обязательно для ввода");
            }
        }
        vehicle.setCoordinates(coordinates);
    }

    public void setEnginePower (Scanner scanner){
        if(ScriptDecoder.getInstance().isScriptStart())System.out.println("Введите силу двигателя");
        while (true) {
            try {
                handle(scanner,s->Float.parseFloat(s) > 0.0f,
                        s->vehicle.setEnginePower(Float.parseFloat(s)),
                        "сила двигателя должна быть больше 0",
                        s->!s.equals(""),
                        ()-> vehicle.setEnginePower(null));
            } catch (NumberFormatException e) {
                System.out.println("неверный тип данных");
                continue;
            }
            break;
        }
    }

    public void setNumberOfWheels (Scanner scanner){
        if(ScriptDecoder.getInstance().isScriptStart())System.out.println("Введите кол-во колес");
        while (true) {
            try {
                handle(scanner,s->Integer.parseInt(s) > 0,
                        s->vehicle.setNumberOfWheels(Integer.parseInt(s)),
                        "кол-во колес должно быть больше 0",
                        s->!s.equals(""),
                        ()-> vehicle.setNumberOfWheels(null));
            } catch (NumberFormatException e) {
                System.out.println("неверный тип данных");
                continue;
            }
            break;
        }
    }

    public void setType (Scanner scanner){
        if(ScriptDecoder.getInstance().isScriptStart())System.out.println("Введите тип транспорта:\n    CAR,\n    PLANE,\n    DRONE,\n    MOTORCYCLE,\n    SPACESHIP");
        try{
            handle(scanner,s->s.equals("CAR") | s.equals("PLANE") | s.equals("DRONE") | s.equals("MOTORCYCLE") | s.equals("SPACESHIP"),
                    s->vehicle.setType(VehicleType.valueOf(s)),
                    "неверный тип данных, введите что-то из списка:\n    CAR,\n    PLANE,\n    DRONE,\n    MOTORCYCLE,\n    SPACESHIP",
                    s->!s.equals(""),
                    ()->vehicle.setType(null));
        }catch (IllegalArgumentException e){
            System.out.println("Неверный или пустой ввод. Для данного поля выставлено пустое значение");
            vehicle.setType(null);
        }
    }

    public void setFuelType (Scanner scanner){
        if(ScriptDecoder.getInstance().isScriptStart())System.out.println("Введите тип топлива\n    ELECTRICITY,\n    NUCLEAR,\n    ANTIMATTER");
        handle(scanner,s->(s.equals("ELECTRICITY")|s.equals("NUCLEAR") | s.equals("ANTIMATTER")),
                s->vehicle.setFuelType(FuelType.valueOf(s)),
                "неверный тип данных, введите что-то из списка:\n    ELECTRICITY,\n    NUCLEAR,\n    ANTIMATTER",
                s->true,System.out::println);
    }




    public Data createPacket (Data data) {
        try {
            System.out.println("Создание, изменение или удаление объекта с номером " + data.getSecondWord());
            data.setVehicleCreator(null);
            Data dataInstance = Data.getInstance();
            Scanner scanner = dataInstance.getScanner();
            setId();
            setName(scanner);
            setCoordinates(scanner);
            vehicle.setCreationDate(new Date());
            setEnginePower(scanner);
            setNumberOfWheels(scanner);
            setType(scanner);
            setFuelType(scanner);
            data.setVehicleCreator(vehicle);
        } catch (NoSuchElementException e) {
            System.out.println("Команда с вводом Vehicle имеет неполные или некорректны данные. Дополните скрипт");
        }
        return data;
    }

}
