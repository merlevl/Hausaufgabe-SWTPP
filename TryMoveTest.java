package de.tuberlin.sese.swtpp.gameserver.test.deathstacks;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.deathstacks.DeathStacksGame;

public class TryMoveTest {

	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");

	Player redPlayer = null;
	Player bluePlayer = null;
	DeathStacksGame game = null;
	GameController controller;

	String gameType = "deathstacks";

	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();

		int gameID = controller.startGame(user1, "", gameType);

		game = (DeathStacksGame) controller.getGame(gameID);
		redPlayer = game.getPlayer(user1);

	}

	public void startGame(String initialBoard, boolean redNext) {
		controller.joinGame(user2, gameType);
		bluePlayer = game.getPlayer(user2);

		game.setBoard(initialBoard);
		game.setNextPlayer(redNext ? redPlayer : bluePlayer);
	}

	public void assertMove(String move, boolean red, boolean expectedResult) {
		if (red)
			assertEquals(expectedResult, game.tryMove(move, redPlayer));
		else
			assertEquals(expectedResult, game.tryMove(move, bluePlayer));
	}

	public void assertGameState(String expectedBoard, boolean redNext, boolean finished, boolean draw, boolean redWon) {
		String board = game.getBoard();

		assertEquals(expectedBoard, board);
		assertEquals(finished, game.isFinished());
		if (!game.isFinished()) {
			assertEquals(redNext, game.isRedNext());
		} else {
			assertEquals(draw, game.isDraw());
			if (!draw) {
				assertEquals(redWon, redPlayer.isWinner());
				assertEquals(!redWon, bluePlayer.isWinner());
			}
		}
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/

	@Test
	public void exampleTest() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true);
		assertMove("d6-1-d4", true, false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true, false, false, false);
	}
	
	@Test
	public void ourTest1() {
		
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", false);

		//Feld nicht auf Board
		assertMove("a9-1-d2", false, false);
		
		// Rot bewegt, ist aber nicht dran
		assertMove("a6-1-b6", true, false);
		
		// keine �nderung des board states, da 10 Schritte
		assertMove("e6-10-d6", false, false);

		// startfield == endfield
		assertMove("f1-1-f1", false, false);
		
		// blau will feld von rot bewegen
		assertMove("a2-1-a1", false, false);
		
		
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false, false,false, false);

	}
	
	@Test
	public void ourTest2() {
		
		startGame("bbrr,bbrr,bbrbrr,bbrr,bbrr,rbr/,,,,,b/,,,,,/,,,,,/,,,,,/,,,,,", false);

		// tooTall, ung�ltig, da kein valid move
		assertMove("c6-3-c5", false, false);

		// tooTall, ung�ltig, da updated startfield zu hoch
		assertMove("c6-1-c5", false, false);	
		
		// tooTall, g�ltig, da zu hohes field bewegt wird (blau)
		assertMove("c6-2-e6", false, true);
		
		// tooTall, g�ltig, da zu hohes field bewegt wird (rot)
		assertMove("c6-2-a4", true, true);
		
		assertGameState("bbrr,bbrr,rr,bbrr,bbbbrr,rbr/,,,,,b/rb,,,,,/,,,,,/,,,,,/,,,,,", false, false, false, false);
	}
	
	@Test
	public void ourTest3() {
		
		
		startGame("rrbb,rrbb,rrbrbb,rrbb,rrbb,brb/,,,,,r/,,,,,/,,,,,/,,,,,/,,,,,", false);
		
		// tooTall, g�ltig, da zu hohes field von anderem spieler (rot)
		assertMove("f6-2-f4", false, true);
		
		assertGameState("rrbb,rrbb,rrbrbb,rrbb,rrbb,b/,,,,,r/,,,,,br/,,,,,/,,,,,/,,,,,", true, false, false, false);
					
	}
		
	
	
	@Test
	public void ourTest4() {
	// tooTall, g�ltig, da zu hohes field bewegt wird
			// blau gewinnt
			startGame("bbrr,bbrr,bbrbrr,bbrr,bbrr,rbr/,,,,,b/,,,,,/,,,,,/,,,,,/,,,,,", false);
			assertMove("c6-3-f6", false, true);
			assertGameState("bbrr,bbrr,brr,bbrr,bbrr,bbrrbr/,,,,,b/,,,,,/,,,,,/,,,,,/,,,,,", false, true, false, false);

	}
	
	@Test
	public void ourTest5() {
			// tooTall, g�ltig, da zu hohes field bewegt wird
			// rot gewinnt
			startGame("rrbb,rrbb,rrbrbb,rrbb,rrbb,brb/,,,,,r/,,,,,/,,,,,/,,,,,/,,,,,", true);
			assertMove("c6-3-f6", true, true);
			assertGameState("rrbb,rrbb,rbb,rrbb,rrbb,rrbbrb/,,,,,r/,,,,,/,,,,,/,,,,,/,,,,,", true, true, false, true);	//muss redWon true sein
	}

	
	@Test
	public void ourTest6() {
			// blueWon, finished
			startGame("bbrr,bbrr,bbrr,bbrr,bbrr,rbr/,,,,,b/,,,,,/,,,,,/,,,,,/,,,,,", false);
			assertMove("f5-1-f6", false, true);
			assertGameState("bbrr,bbrr,bbrr,bbrr,bbrr,brbr/,,,,,/,,,,,/,,,,,/,,,,,/,,,,,", false, true, false, false);
	
	}
	
	@Test
	public void ourTest7() {
	// draw, wegen repeating state
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);		 
		assertMove("d6-1-d5",true,true);	
		assertMove("d1-1-d2",false,true);
		assertMove("d5-1-d6",true,true);	
		assertMove("d2-1-d1",false,true);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false,true,true,false);
	
	
	}
	
	
	@Test
	public void ourTest8() {
		
		startGame(",bbrr,bbrr,bbrr,bbrr,rbr/bbrr,,,,,b/,,,,,/,,,,,/,,,,,/,,,,,", false);
	
		assertMove("a5-3-a4", false, true);
//		vertic raus oben, korrekt
//		",bbrr,bbrr,bbrr,bbrr,rbr/r,,,,,b/bbr,,,,,/,,,,,/,,,,,/,,,,," -> rot dran
	
		assertMove("f6-1-f5", true, true); 
//		vertic sminus
//		",bbrr,bbrr,bbrr,bbrr,br/r,,,,,rb/bbr,,,,,/,,,,,/,,,,,/,,,,,"
	
		assertMove("e6-1-f6", false, true); 
//		horiz splus
//		",bbrr,bbrr,bbrr,brr,bbr/r,,,,,rb/bbr,,,,,/,,,,,/,,,,,/,,,,,"
	
		assertMove("a5-1-a6", true, true);
//		vertic plus
//		"r,bbrr,bbrr,bbrr,brr,bbr/,,,,,rb/bbr,,,,,/,,,,,/,,,,,/,,,,,"
	
		assertMove("b6-3-c6", false, true); 
//		horiz minus raus
//		"r,r,bbrbbrr,bbrr,brr,bbr/,,,,,rb/bbr,,,,,/,,,,,/,,,,,/,,,,,"
	
		assertMove("b6-1-a6", true, true); 
//		horiz minus
//		"rr,,bbrbbrr,bbrr,brr,bbr/,,,,,rb/bbr,,,,,/,,,,,/,,,,,/,,,,,"
	
		assertMove("c6-6-c2", false, true); 
//		vertic sminus raus
//		"rr,,r,bbrr,brr,bbr/,,,,,rb/bbr,,,,,/,,,,,/,,bbrbbr,,,/,,,,,"
	
		assertMove("a6-2-c4", true, true);
//		diag rechts unten
//		",,r,bbrr,brr,bbr/,,,,,rb/bbr,,rr,,,/,,,,,/,,bbrbbr,,,/,,,,,"
	
		assertMove("c2-4-e4",false, true); 
//		diag: an rand unten von links dann nach rechts hoch; seite von unten nach links oben
//		",,r,bbrr,brr,bbr/,,,,,rb/bbr,,rr,,bbrb,/,,,,,/,,br,,,/,,,,,"
	
		assertMove("c4-2-c6",true,true); 
//		",,rrr,bbrr,brr,bbr/,,,,,rb/bbr,,,,bbrb,/,,,,,/,,br,,,/,,,,,"
	
		assertMove("e6-3-d6",false, true);
//		",,rrr,brrbbrr,,bbr/,,,,,rb/bbr,,,,bbrb,/,,,,,/,,br,,,/,,,,,"
	
		assertMove("c6-3-b3",true,true);
//		diag: nach links an seite, dann nach rechts unten
//		",,,brrbbrr,,bbr/,,,,,rb/bbr,,,,bbrb,/,rrr,,,,/,,br,,,/,,,,,"
	
		assertMove("d6-7-a3",false,true);
//		diag: an seite von rechts oben, dann weiter nach links unten, von rechts oben an rand unten, dann nach links hoch
//		",,,,,bbr/,,,,,rb/bbr,,,,bbrb,/brrbbrr,rrr,,,,/,,br,,,/,,,,,"
	
		assertMove("b3-3-c6",true,true);
//		diag: an linke seite von rechts unten, dann nach rechts oben weiter
//		",,rrr,,,bbr/,,,,,rb/bbr,,,,bbrb,/brrbbrr,,,,,/,,br,,,/,,,,,"
	
		assertMove("a3-4-e5",false,true);
//		diag: von links unten an rand oben, dann nach rechts unten weiter
//		",,rrr,,,bbr/,,,,brrb,rb/bbr,,,,bbrb,/brr,,,,,/,,br,,,/,,,,,"
	
		assertMove("c6-1-b5",true,true);
//		diag: rechts runter
//		",,rr,,,bbr/,r,,,brrb,rb/bbr,,,,bbrb,/brr,,,,,/,,br,,,/,,,,,"
	
		assertMove("e5-3-b4",false,true);
//		diag: von links unten an rand oben, dann nach rechts unten weiter
//		",,rr,,,bbr/,r,,,b,rb/bbr,brr,,,bbrb,/brr,,,,,/,,br,,,/,,,,,"
	
		assertMove("b5-1-a6",true,true);
//		diag: nach links oben
	
		assertMove("e4-1-f5",false,true);
//		"r,,rr,,,bbr/,,,,b,brb/bbr,brr,,,brb,/brr,,,,,/,,br,,,/,,,,,"
//		diag: nach rechts oben
	
		assertMove("c6-1-b6",true,true); 
//		"r,r,r,,,bbr/,,,,b,brb/bbr,brr,,,brb,/brr,,,,,/,,br,,,/,,,,,"

		assertMove("b4-3-e4",false,true);
//		"r,r,r,,,bbr/,,,,b,brb/,brr,,,bbrbrb,/brr,,,,,/,,br,,,/,,,,,"

		assertMove("c6-1-b5",true,true); 
//		"r,r,,,,bbr/,r,,,b,brb/,brr,,,bbrbrb,/brr,,,,,/,,br,,,/,,,,,"

		assertMove("e4-3-d4",false,true);
//		horiz plus raus
//		"r,r,,,,bbr/,r,,,b,brb/,brr,,bbr,brb,/brr,,,,,/,,br,,,/,,,,,"
	
		assertMove("a6-1-b6",true,true);
//		",rr,,,,bbr/,r,,,b,brb/,brr,,bbr,brb,/brr,,,,,/,,br,,,/,,,,,"

		assertMove("a3-3-d2",false,true);
//		",rr,,,,bbr/,r,,,b,brb/,brr,,bbr,brb,/,,,,,/,,br,brr,,/,,,,,"

		assertMove("b5-1-b6",true,true);
//		",rrr,,,,bbr/,,,,b,brb/,brr,,bbr,brb,/,,,,,/,,br,brr,,/,,,,,"
	
		assertMove("d2-3-d3",false,true);
//		vertic minus raus
//		",rrr,,,,bbr/,,,,b,brb/,brr,,bbr,brb,/,,,brr,,/,,br,,,/,,,,,"

		assertGameState(",rrr,,,,bbr/,,,,b,brb/bbr,,,brr,brb,/,,,brr,,/,,br,,,/,,,,,", true, false, false, false);
}	
	
	@Test
	public void ourTest9() {
		
		//diagonal nach rechts unten
		startGame("bbrr,bbrr,bbrr,bbrr,bbrr,rbr/,,,,,b/,,,,,/,,,,,/,,,,,/,,,,,", false);
		assertMove("c6-2-e4", false, true );
		assertGameState("bbrr,bbrr,rr,bbrr,bbrr,rbr/,,,,,b/,,,,bb,/,,,,,/,,,,,/,,,,,", true, false, false, false);
	}
	
	
	@Test
	public void ourTest910() {
	//diagonal nach links unten
		startGame("bb,bbrr,rr,bbrr,bbrr,brbr/,,,,,/,,,,rr,/,,,,,/,,,,,/bb,,,,,", true);
		assertMove("e4-2-c2", true, true);
		assertGameState("bb,bbrr,rr,bbrr,bbrr,brbr/,,,,,/,,,,,/,,,,,/,,rr,,,/bb,,,,,", false, false, false, false);
	}
	
	@Test
	public void ourTest911() {
	//diagonal nach links oben
		startGame("bbrr,bbrr,rr,bbrr,bbrr,brbr/,,,,,/,,,,,/,,,,,/,,bb,,,/,,,,,", false);
		assertMove("c2-2-a4", false, true);
		assertGameState("bbrr,bbrr,rr,bbrr,bbrr,brbr/,,,,,/bb,,,,,/,,,,,/,,,,,/,,,,,", true, false, false, false);
	}
	
	@Test
	public void ourTest912() {
	//diagonal nach rechts oben
		startGame("brr,bbrr,bb,rrbb,bbrr,rbr/,,,,,/rrbb,,,,,/,,,,,/,,,,,/,,,,,", true);
		assertMove("a4-2-c6", true, true);
		assertGameState("brr,bbrr,rrbb,rrbb,bbrr,rbr/,,,,,/bb,,,,,/,,,,,/,,,,,/,,,,,", false, false, false, false);
	}
	
	@Test
	public void ourTest913() {
	//am rechten rand von oben spiegelnd
		startGame("bbrr,bbrr,bbrr,bbrr,bbrr,rbbr/,,,,,/,,,,,/,,,,,/,,,,,/,,,,,", false);
		assertMove("e6-4-c2", false, true);
		assertGameState("bbrr,bbrr,bbrr,bbrr,,rbbr/,,,,,/,,,,,/,,,,,/,,bbrr,,,/,,,,,", true, false, false, false);
	}
	
	@Test
	public void ourTest914() {
	//am rechten rand von unten spiegelnd
		startGame("bbrr,bbrr,bbrr,bbrr,,brbr/,,,,,/,,,,,/,,,,,/,,rrbb,,,/,,,,,", true);
		assertMove("c2-4-e6", true, true);
		assertGameState("bbrr,bbrr,bbrr,bbrr,rrbb,brbr/,,,,,/,,,,,/,,,,,/,,,,,/,,,,,", false, false, false, false);
	}
	
	@Test
	public void ourTest915() {
	//am unteren rand spiegelnd von rechts
		startGame("bbr,bbrr,rr,bbrr,bbrr,brbr/,,,,,/,,,,,/,,,,,/,,,bbr,,/,,,,,", false);
		assertMove("d2-3-a2", false, true);
		assertGameState("bbr,bbrr,rr,bbrr,bbrr,brbr/,,,,,/,,,,,/,,,,,/bbr,,,,,/,,,,,", true, false, false, false);
	}
	
	@Test
	public void ourTest916() {
	//am unteren rand spiegelnd von links
		startGame("bbrr,bbrr,b,bbrr,bbrr,brbr/,,,,,/,,,,,/,,,,,/,,rrb,,,/,,,,,,", true);
		assertMove("c2-3-f3", true, true);
		assertGameState("bbrr,bbrr,b,bbrr,bbrr,brbr/,,,,,/,,,,,/,,,,,rrb/,,,,,/,,,,,,", false, false, false, false);
	}
	
	@Test
	public void ourTest917() {
	//am linken rand von unten spiegelnd
		startGame("bbrr,bbrr,bbrr,r,bbrr,rbr/,,,,,b/,,,,,/,,,,,/,bbr,,,,/,,,,,", false);
		assertMove("b2-3-c5", false, true);
		assertGameState("bbrr,bbrr,bbrr,r,bbrr,rbr/,,bbr,,,b/,,,,,/,,,,,/,,,,,/,,,,,", true, false, false, false);
	}
	
	@Test
	public void ourTest918() {
	//am linken rand von oben spiegelnd
		startGame("bbrr,bbrr,r,bbrr,bbrr,brbr/,,rbb,,,/,,,,,/,,,,,/,,,,,/,,,,,", true);
		assertMove("c5-3-b2", true, true);
		assertGameState("bbrr,bbrr,r,bbrr,bbrr,brbr/,,,,,/,,,,,/,,,,,/,rbb,,,,/,,,,,", false, false, false, false);
	}
	
	@Test
	public void ourTest919() {
	//am oberen rand spiegelnd von rechts
		startGame("r,bbrr,bbrr,bbrr,bbrr,brbr/,,,bbr,,/,,,,,/,,,,,/,,,,,/,,,,,", false);
		assertMove("d5-3-a5", false, true);
		assertGameState("r,bbrr,bbrr,bbrr,bbrr,brbr/bbr,,,,,/,,,,,/,,,,,/,,,,,/,,,,,", true, false, false, false);
	}
	
	@Test
	public void ourTest920() {
	//am oberen rand spiegelnd von links
		startGame("bbrr,bbrr,bbrr,bbrr,,brbr/,,rrb,,,b/,,,,,/,,,,,/,,,,,/,,,,,", true);
		assertMove("c5-3-f4", true, true);
		assertGameState("bbrr,bbrr,bbrr,bbrr,,brbr/,,,,,b/,,,,,rrb/,,,,,/,,,,,/,,,,,", false, false, false, false);
	}
	
	@Test
	public void ourTest921() {
	//am rechten und dann am unteren rand spiegelnd
		startGame("bbrr,bbrr,bbrr,bbrr,,rb/,,,,,/,,,,brrbrb,/,,,,,/,,,,,/,,,,,", false);
		assertMove("e4-5-b3", false, true);
		assertGameState("bbrr,bbrr,bbrr,bbrr,,rb/,,,,,/,,,,b,/,brrbr,,,,/,,,,,/,,,,,", true, false, false, false);
	}
	
	@Test
	public void ourTest922() {
	//am unteren und dann am linken rand spiegelnd
		startGame("r,bbrr,bbr,bb,,brr/,,,,r,bb/,,,,,/,,,,,/,,,,rbrbrbrr,/,,,,,", true);
		assertMove("e2-5-d5", true, true);
		assertGameState("r,bbrr,bbr,bb,,brr/,,,rbrbr,r,bb/,,,,,/,,,,,/,,,,brr,/,,,,,", false, false, false, false);
	}
	
	@Test
	public void ourTest923() {
	//am linken und dann am oberen rand spiegelnd
		startGame("bbrr,bbr,bbrr,bbrr,,rr/,,,,,b/,,,,,/,,,,,/,,,,,/,,bbbrrr,,,", false);
		assertMove("c1-6-e5", false, true);
		assertGameState("bbrr,bbr,bbrr,bbrr,,rr/,,,,bbbrrr,b/,,,,,/,,,,,/,,,,,/,,,,,", true, false, false, false);
	}
	
	@Test
	public void ourTest924() {
	//am oberen und dann am rechten rand spiegelnd
		startGame("bbrr,bbrr,bbrr,bbr,,brbr/,,,rrr,,bb/,,,,,/,,,,,/,,,,,/,,,,,", true);
		assertMove("d5-3-e4", true, true);
		assertGameState("bbrr,bbrr,bbrr,bbr,,brbr/,,,,,bb/,,,,rrr,/,,,,,/,,,,,/,,,,,", false, false, false, false);
	}
	
}