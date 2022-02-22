package ver3;

import ver3.server_info.AppSocket;
import ver3.server_info.players.Player;
import ver3.server_info.players.PlayerAI;
import ver3.server_info.players.PlayerNet;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Document   : Tests
 * Created on : 10/11/2021, 13:20:27
 * Author     : ilan
 */
public class Tests {
    public static void main(String[] args) {
        //Player p1 = new Player("ddd");  /// >>> Error > Player is abstract; cannot be instantiated!

        PlayerNet p1 = new PlayerNet(new AppSocket(new Socket()), "moshe");
        PlayerAI p2 = new PlayerAI(15, "AI");

        ArrayList<Player> playersList = new ArrayList();
        playersList.add(p1);
        playersList.add(p2);

        for (int i = 0; i < playersList.size(); i++) {
            Player p = playersList.get(i);
            System.out.println(p.getId());
            System.out.println(p.getMove());

//            if(p instanceof PlayerNet)
//                ((PlayerNet) p).getSocketToClient();
//            
//            if(p instanceof PlayerAI)
//                ((PlayerAI) p).getMinimaxTimeout();

        }

    }
}
