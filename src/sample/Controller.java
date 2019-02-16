package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable
{
	@FXML
	private GridPane myGrid;
	@FXML
	private Pane myFirstPane;
	@FXML
	private Pane mySecondPane;
	@FXML
	public Label playerLabel;
	@FXML
	public TextField textFieldOne;
	@FXML
	public TextField textFieldTwo;
	@FXML
	public Button setNames;

	private static final int column = 7;
	private static final int row = 6;
	private static final int diameter = 80;

	public static String PlayerOne = "Player One";
	public static String PlayerTwo = "Player Two";

	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "4CAA88";

	private Disc[][] discArray = new Disc[row][column];

	private boolean turn = true;
	private boolean allowance = true;


	//Main main = new Main();

	public void createPlaygroud() {

		Shape mainRec = createMainStructure();
		myGrid.add(mainRec, 0, 1);

		List<Rectangle> rectangleList = createSecondaryStructure();
		for (Rectangle rectangle : rectangleList)
		{
			myGrid.add(rectangle, 0, 1);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}
	private Shape createMainStructure()
	{
		Shape mainRec = new Rectangle(diameter*(column+1), diameter*(row+1));
		for (int i = 0; i <row ; i++)
		{
			for (int j = 0 ; j<column ; j++)
			{
				Circle circle = new Circle(diameter/2);
				circle.setCenterX(diameter/2);
				circle.setCenterY(diameter/2);
				circle.setSmooth(true);

				circle.setTranslateX(j*(diameter+5) + 20);
				circle.setTranslateY(i*(diameter+5) + 20);

				mainRec = Shape.subtract(mainRec,circle);
			}

		}

		mainRec.setFill(Color.WHITE);
		return mainRec;
	}

	private List<Rectangle> createSecondaryStructure()
	{
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int i = 0; i <column ; i++)
		{
			final int col;
			col = i;
			Rectangle rectangle = new Rectangle(diameter, diameter * (row + 1));
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(i*(diameter+5) + 20);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			rectangle.setOnMouseClicked(event ->
			{
				if(allowance)
				{
					allowance = false;
					insertDisk(new Disc(turn), col);
				}
			});
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}
		private void insertDisk(Disc disc, int COL)
		{

			int ROW = row-1;
			while (ROW>=0)
			{
				if (checkDisc(ROW, COL)==null)
					break;
				ROW--;
			}
			if (ROW<0)
				return;
			int currentRow = ROW;
			discArray[ROW][COL] = disc;
			mySecondPane.getChildren().addAll(disc);

			disc.setTranslateX(COL * (diameter + 5) + 20);
			TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), disc);
			transition.setToY(ROW * (diameter + 5) + 20);
			transition.setOnFinished(event ->
			{
				allowance = true;
				if(GameEnds(currentRow, COL))
				{
					gameOver();
					return ;
				}
				turn = !turn;
				playerLabel.setText(turn? PlayerOne : PlayerTwo);
			});
			transition.play();

		}

	private void gameOver()
	{
		String winner = turn? PlayerOne : PlayerTwo;
		Alert winDialog = new Alert(Alert.AlertType.INFORMATION);
		winDialog.setTitle("Winner");
		winDialog.setHeaderText("The winner of the tournament is : " + winner);
		winDialog.setContentText("Do you want to play the game again?");

		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType("No");
		winDialog.getButtonTypes().setAll(yesButton,noButton);
		Platform.runLater(()->{
		Optional<ButtonType> checkButton = winDialog.showAndWait();
		if (checkButton.isPresent()==true && checkButton.get()==yesButton)
		{
			reset();
		}
		else if (checkButton.isPresent()==true && checkButton.get()==noButton)
		{
			Platform.exit();
			System.exit(0);
		}
	});

	}

	public void reset()
	{
		mySecondPane.getChildren().clear();
		for (int i=0 ; i<discArray.length ; i++)
		{
			for (int j=0 ; j<discArray[i].length ; j++)
			{
				discArray [i][j] =null;
			}
		}
		turn = true;
		playerLabel.setText(PlayerOne);
		createPlaygroud();
	}

	private boolean GameEnds(int currentRow, int COL)
	{
		List<Point2D> verticalPoints = IntStream.rangeClosed(currentRow-3,currentRow+3).mapToObj(r -> new Point2D(r, COL)).collect(Collectors.toList());
		List<Point2D> horizontalPoints = IntStream.rangeClosed(COL-3, COL+3).mapToObj(c -> new Point2D(currentRow, c)).collect(Collectors.toList());

		Point2D start1 = new Point2D(currentRow-3, COL-3);
		List<Point2D> diagonalPoints1 = IntStream.rangeClosed(0,6).mapToObj(i -> start1.add(i,i)).collect(Collectors.toList());

		Point2D start2 = new Point2D(currentRow-3, COL+3);
		List<Point2D> diagonalPoints2 = IntStream.rangeClosed(0,6).mapToObj(i -> start2.add(i,-i)).collect(Collectors.toList());

		boolean Result = qualityCheck(verticalPoints) || qualityCheck(horizontalPoints) || qualityCheck(diagonalPoints1) || qualityCheck(diagonalPoints2);
		return Result;
	}

	private boolean qualityCheck(List<Point2D> Points)
	{
		int chain = 0;
		for (Point2D point : Points)
		{
			int rowIndex = (int) point.getX();
			int colIndex = (int) point.getY();

			Disc disc = checkDisc(rowIndex,colIndex);

			if (disc!=null && disc.turn==turn)
			{
				chain++;
				if (chain==4)
				{
					return true;
				}
			}

		}
		return false;
	}

	private Disc checkDisc(int rowIndex, int colIndex)
	{
		if (rowIndex>=row || rowIndex<0 || colIndex>=column || colIndex<0)
			return null;
		else
			return discArray[rowIndex][colIndex];
	}

	private static class Disc extends Circle
		{
			private final boolean turn;
			public Disc(boolean turn)
			{
				this.turn = turn;
				setRadius(diameter/2);
				setFill(turn? Color.valueOf(discColor1) : Color.valueOf(discColor2));
				setCenterX(diameter/2);
				setCenterY(diameter/2);
			}
		}
}
