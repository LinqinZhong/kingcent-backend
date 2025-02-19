package com.kingcent.net;

public class Logger {

    public static void logo(){
        System.out.println("""
                                   [#]
                                   /
                                  /
                          -------/------
                        /      [#]       \\
                       /       /          \\
                       |      /           |
                       |     /            |
                        \\   /            /
                         \\ /            /
                          /------------
                         /
                        /
                       /
                    #######
                    
                    Kingcent NetCable
                    Designed by LinqinZhong(https://github.com/LinqinZhong)
                    Version: 1.0
                    """);
    }

    public static void green(String msg){
        System.out.println("\u001B[0;32m"+msg+"\033[0m");
    }

    public static void yellow(String msg){
        System.out.println("\u001B[0;33m"+msg+"\033[0m");
    }

    public static void greenUnderline(String msg){
        System.out.println("\u001B[4;32m"+msg+"\033[0m");
    }

    public static void greenBold(String msg){
        System.out.println("\u001B[1;32m"+msg+"\033[0m");
    }

    public static void red(String msg){
        System.out.println("\u001B[0;31m"+msg+"\033[0m");
    }

    public static void blue(String msg){
        System.out.println("\u001B[0;34m"+msg+"\033[0m");
    }


    public static void redUnderline(String msg){
        System.out.println("\u001B[4;31m"+msg+"\033[0m");
    }
}
