package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.ArrayList;

//Controller class
@SuppressWarnings("unchecked")
public class Controller {

    //FMXL objects
    @FXML
    TextField lenght;
    @FXML
    TextField height;

    //Scatter chart
    @FXML
    NumberAxis xAxis = new NumberAxis();
    @FXML
    NumberAxis yAxis = new NumberAxis();
    @FXML
    ScatterChart<Double, Double> chart;

    @FXML
    Label result;

    //Initialization
    public void initialize() {
        xAxis.setLabel("Angle (°)");
        yAxis.setLabel("Length (m)");

    }


    //On button and enter click
    //Does calculations and displays them
    @FXML
    public void onButton(ActionEvent actionEvent) {
        //Error handling on input text input
        if (lenght.getText().isEmpty() || height.getText().isEmpty() ||
                isNotNumeric(lenght.getText()) || isNotNumeric(height.getText()) ||
                Double.parseDouble(lenght.getText()) < 0 || Double.parseDouble(height.getText()) < 0) {
            result.setText("WARNING: Please enter valid height and string length values!");
            result.setTextFill(Color.RED);
            return;
        }

        //Gets a double value from the text inputs
        double heightValue = Double.parseDouble(height.getText());
        double lenghtValue = Double.parseDouble(lenght.getText());


        //Error handling
        if (lenghtValue > heightValue) {
            result.setText("WARNING: String length can not be longer than the height!");
            result.setTextFill(Color.RED);
            return;
        } else {
            //Set black text color
            result.setTextFill(Color.BLACK);
        }

        //Array with the resulting values
        ArrayList<Double> resultArray = new ArrayList<>();

        //Create a new data series for scatter chart
        XYChart.Series<Double, Double> series = new XYChart.Series();
        //Set the name of series
        series.setName("h: " + heightValue + " l: " + lenghtValue);

        //Loop through angles 0 to 90 and add results to array and series
        for (int i = 0; i < 90; i++) {
            resultArray.add(getSX(i, lenghtValue, heightValue));
            series.getData().add(new Data<>((double) i, getSX(i, lenghtValue, heightValue)));
        }

        //Get the optimal angle and set result label
        result.setText(getOptimalAngle(resultArray));

        //Add series to chart
        chart.getData().addAll(series);


    }

    //Tests if the string inputted is not a number
    //Returns true if it is not numerical
    private boolean isNotNumeric(String text) {
        {
            try {
                double d = Double.parseDouble(text);
            } catch (NumberFormatException nfe) {
                return true;
            }
            return false;
        }

    }

    //Gets the optimal angle and its length from the results array
    private String getOptimalAngle(ArrayList<Double> list) {
        //Index and index value get returned
        int index = 0;
        double indexValue = list.get(0);
        //Loops through and changes value if one is larger
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) > indexValue) {
                index = i;
                indexValue = list.get(i);
            }
        }

        //For rounding the output value
        int temp = (int) (indexValue * 1000);
        indexValue = temp;
        indexValue = indexValue / 1000;

        //Returning string
        return "Optimal angle: " + index + "°, optimum length: " + indexValue + " meters";


    }

    //Reset button
    @FXML
    private void reset(ActionEvent actionEvent) {
        //Clears and resets everything
        chart.getData().clear();
        lenght.clear();
        height.clear();
        result.setText("");
        //Focuses the first input box
        lenght.requestFocus();

    }

    /*Gets the length from the center of the pendulum to the floor
    Angle is the angle from the resting position of the pendulum meaning if it is its resting posision (no movement) the angle would be 0 degrees. Max is 90 degrees.

    l is the length of the string of the pendulum

    h is the distance from the floor to the pivot point ie the height of the pivot point
    */
    private double getSX(double angle, double l, double h) {
        //Gets the height at specific angle
        double hr = l * (Math.cos(Math.toRadians(angle) - Math.cos(Math.toRadians(270)))) + h;
        //Force of gravity
        double g = 9.82;
        //Kinetic energy at specific angle
        double KE = Math.sin(Math.toRadians(angle) + Math.toRadians(90)) * l * g;
        //Initial velocity at specific angle
        double V0 = Math.sqrt(2 * KE);
        //Initial velocity in x direction at specific angle
        double VOX = Math.cos(Math.toRadians(angle)) * V0;
        //Initial velocity in y direction at specific angle
        double V0Y = Math.sin(Math.toRadians(angle)) * V0;
        //Time that the projectile stays in the air. Depends on the initial velocity in the y direction and initial height
        double t = (-V0Y - (Math.sqrt((Math.pow(V0Y, 2)) + (2 * g * hr)))) / -g;
        //Gets the displacement in the x direction after "launch"
        double SX = t * VOX;
        //Gets the initial displacement from the center of the pendulum
        double DX = Math.sin(Math.toRadians(angle)) * l;

        //Returns the total displacement of the projectile
        return SX + DX;

    }
}
