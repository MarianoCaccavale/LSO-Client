package com.example.potholeclient.utils;

import com.example.potholeclient.models.PotholesModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;

public class Network {

    public static void sendData(String nickname, double latitude, double longitude){

        //Creo il socket a cui mandare i dati
        try {
            Socket socket = new Socket(Costants.ip, Costants.port);

            //Debug
            //String dati = "Charlie;78.000015;96.369874";

            String post = "post";
            socket.getOutputStream().write(post.getBytes());

            String dati = nickname+";"+latitude+";"+longitude;
            socket.getOutputStream().write(dati.getBytes());

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LinkedList<PotholesModel> receiveData(String myNickname, double myLatitude, double myLongitude){

        LinkedList<PotholesModel> resultList = new LinkedList<>();

        try {
            Socket socket = new Socket(Costants.ip, Costants.port);

            String get = "get";
            socket.getOutputStream().write(get.getBytes());

            BufferedReader stdIn =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

            String dati = myNickname+";"+myLatitude+";"+myLongitude;
            socket.getOutputStream().write(dati.getBytes());

            Thread.sleep(500);

            String responseBuffer;
            while(stdIn.ready()){
                responseBuffer = stdIn.readLine();
                responseBuffer = responseBuffer.replace("\u0000", "");
                if(responseBuffer.isEmpty()){
                    break;
                }
                String[] fields = responseBuffer.split(";");
                String nickname = fields[0];
                double latitude = Double.parseDouble(fields[1]);
                double longitude = Double.parseDouble(fields[2]);

                resultList.add(new PotholesModel(nickname, latitude, longitude));

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public static double getTollerance(){

        String getTollerance = "toll";
        String tolleranceString = "0.000000";

        try {
            Socket socket = new Socket(Costants.ip, Costants.port);
            BufferedReader stdIn =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

            socket.getOutputStream().write(getTollerance.getBytes());

            if(stdIn.ready()){
                tolleranceString = stdIn.readLine();
                tolleranceString = tolleranceString.replace("\u0000", "");
                tolleranceString = tolleranceString.replace(";", "");
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Double.parseDouble(tolleranceString);
    }

}
