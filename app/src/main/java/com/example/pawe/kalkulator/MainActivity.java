package com.example.pawe.kalkulator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private final String DIGITS = "0123456789";
    private final String OPERATORS = "-+*/";
    private final Character DOT = '.';
    private final String TAG = "Kalkulator";
    private final int REQUEST_CODE = 1;
    private ArrayList<StringBuilder> history;
    private TextView outcomeTextView;
    private TextView calculationsTextView;
    private StringBuilder output;
    private boolean isLoneZero;
    private int loneZeroIndex;
    private boolean startNewOutput;
    private ArrayList<Integer> negativeNumbersIndexes;
    private boolean isDot;
    public final static String EXTRA_MESSAGE = "com.example.pawe.kalkulator.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        outcomeTextView = (TextView)findViewById(R.id.textViewOutcome);
        calculationsTextView = (TextView) findViewById(R.id.textViewCalculations);
        isLoneZero = false; //jesli jest samotne 0 lub 0. to true
        isDot = false; // jesli jest kropka w ciagu cyfr to true
        loneZeroIndex = -1; // index samotnego zera 0 lub 0.
        negativeNumbersIndexes = new ArrayList<>(); //lista liczb ujemnych
        output = new StringBuilder(30);
        history = new ArrayList<>();
    }
    public void buttonClick(View view) {

        Button button = (Button)view;
        String buttonText = (String) button.getText();
        if (startNewOutput) {
            Log.e(TAG,"startNewOutput : "+buttonText);
            isLoneZero = false;
            isDot = true;
            loneZeroIndex = -1;
            negativeNumbersIndexes.clear();
        }
        if (output.length() == 14) {
            outcomeTextView.setTextSize(25);
        }
        else if (output.length() < 14) {
            outcomeTextView.setTextSize(40);
        }
        if (buttonText.equals("DEL")) {
            startNewOutput = false;
            Log.e(TAG,"buttonClick : "+buttonText);
            if (output.length() == 0 ) {
                //do nothing
            } else {
                if (output.charAt(output.length()-1) == DOT) {
                    isDot = false;
                }
                else if (output.length()-1 == loneZeroIndex) {
                    isLoneZero = false;
                    loneZeroIndex = -1;
                }
                else if (output.charAt(output.length()-1) == '-' && negativeNumbersIndexes.contains(output.length()-1)) {
                    negativeNumbersIndexes.remove(negativeNumbersIndexes.indexOf(output.length()-1));
                }
                output.deleteCharAt(output.length()-1);
            }
        }
        else if (buttonText.equals("C")) {
            Log.e(TAG,"buttonClick : "+buttonText);
            startNewOutput = false;
            Log.e(TAG,buttonText);
            output.delete(0,output.length());
            isDot = false;
            isLoneZero = false;
            loneZeroIndex = -1;
            negativeNumbersIndexes.clear();
            calculationsTextView.setText("");
        }
        else if (buttonText.equals("=")) {
            Log.e(TAG,"buttonClick : "+buttonText);
            if (output.length() == 0) {
                //do nothing
                Log.e(TAG,"buttonClick : "+buttonText+ " 1 if");
            }
            else if (output.length() == 1 && output.charAt(output.length()-1) == '-') {
                //do nothing
                Log.e(TAG,"buttonClick : "+buttonText+ " 2 if");
            }
            else if ((OPERATORS.indexOf(output.charAt(output.length()-1)) > -1) && (OPERATORS.indexOf(output.charAt(output.length()-2)) > -1)) {
                Log.e(TAG,"buttonClick : "+buttonText+ " 3 if");
                negativeNumbersIndexes.remove(negativeNumbersIndexes.indexOf(output.length()-1));
                output.deleteCharAt(output.length()-1);
                output.deleteCharAt(output.length()-1);
                StringBuilder result = calculateResult(output);
                calculationsTextView.setText(output);
                output.append(" = "+result);
                history.add(output);
                output = new StringBuilder(result);
                startNewOutput = true;
            }
            else if (OPERATORS.indexOf(output.charAt(output.length()-1)) > -1 || output.charAt(output.length()-1) == DOT) {
                Log.e(TAG,"buttonClick : "+buttonText+ " 4 if");
                output.deleteCharAt(output.length()-1);
                StringBuilder result = calculateResult(output);
                calculationsTextView.setText(output);
                output.append(" = "+result);
                history.add(output);
                output = new StringBuilder(result);
                startNewOutput = true;
            }
            else if (startNewOutput &&  output.charAt(0) == '-') {
                Log.e(TAG,"buttonClick : "+buttonText+ " 5 if");
                negativeNumbersIndexes.add(0);
                StringBuilder result = calculateResult(output);
                calculationsTextView.setText(output);
                output.append(" = "+result);
                history.add(output);
                output = new StringBuilder(result);
                startNewOutput = true;
            } else {
                Log.e(TAG,"buttonClick : "+buttonText+ " else");
                StringBuilder result = calculateResult(output);
                calculationsTextView.setText(output);
                output.append(" = "+result);
                history.add(output);
                output = new StringBuilder(result);
                startNewOutput = true;
            }
            if (output.length() > 14) {
                outcomeTextView.setTextSize(25);
            }
        }
        else if (output.length() >= 50) {
            // do nothin, max length
            return;
        }
        else if (Pattern.matches("[1-9]",buttonText)) {
            Log.e(TAG,"buttonClick : "+buttonText);
            if(isLoneZero && !isDot) {
                output.setCharAt(output.length()-1,buttonText.charAt(0));
                isLoneZero = false;
                loneZeroIndex = -1;
                isDot = false;
            }
            else if (startNewOutput){
                output.delete(0,output.length());
                output.append(buttonText);
                startNewOutput = false;
                isDot = false;
                outcomeTextView.setTextSize(40);
            } else {
                output.append(buttonText);
                isLoneZero = false;
                loneZeroIndex = -1;
            }
        }
        else if (buttonText.equals("0")) {
            Log.e(TAG,"buttonClick : "+buttonText);
            if(output.length() == 0) {
                output.append(buttonText);
                isLoneZero = true;
                loneZeroIndex = output.length()-1;
            }
            else if (startNewOutput){
                output.delete(0,output.length());
                output.append(buttonText);
                startNewOutput = false;
                isLoneZero = true;
                loneZeroIndex = output.length()-1;
                isDot = false;
            }
            else if (output.charAt(output.length()-1) == DOT) {
                output.append(buttonText);
            }
            else if (isLoneZero && isDot) {
                output.append(buttonText);
                isLoneZero = true;
            }
            else if (isLoneZero) {
                //do nothing
            }
            else if (DIGITS.indexOf(output.charAt(output.length()-1)) > -1) {
                output.append(buttonText);
            }
            else if (OPERATORS.indexOf(output.charAt(output.length()-1)) > -1) {
                output.append(buttonText);
                isLoneZero = true;
                loneZeroIndex = output.length()-1;
            }
            else if (isDot) {
                output.append(buttonText);
                isLoneZero = true;
            }
        }
        else if (buttonText.equals("+")) {
            Log.e(TAG,"buttonClick : "+buttonText);
            if (output.length() == 0) {
                //do nothing
            }
            else if (DIGITS.indexOf(output.charAt(output.length()-1)) > -1) {
                output.append(buttonText);
                isLoneZero = false;
                isDot = false;
            }
            else if (output.charAt(output.length()-1) == DOT){
                output.setCharAt(output.length()-1,buttonText.charAt(0));
                isLoneZero = false;
                isDot = false;
            }
            else if ((output.charAt(output.length()-1) == '-') && output.length() == 1 ) {
                //do nothing
            }
            else if ((output.charAt(output.length()-1) == '-') && (output.length() > 1) &&
                    (OPERATORS.indexOf(output.charAt(output.length()-2)) > -1)) {
                output.deleteCharAt(output.length()-1);
                output.setCharAt(output.length()-1,buttonText.charAt(0));
            }
            else if ((OPERATORS.indexOf(output.charAt(output.length()-1)) > -1)) {
                output.setCharAt(output.length()-1,buttonText.charAt(0));
            }
            if (startNewOutput &&  output.charAt(0) == '-') {
                negativeNumbersIndexes.add(0);
            }
            startNewOutput = false;
        }
        else if (buttonText.equals("-")) {
            Log.e(TAG,"buttonClick : "+buttonText);
            if (output.length() == 0) {
                output.append(buttonText);
                negativeNumbersIndexes.add(output.length()-1);
            }
            else if (DIGITS.indexOf(output.charAt(output.length()-1)) > -1) {
                output.append(buttonText);
                isLoneZero = false;
                isDot = false;
            }
            else if ((output.charAt(output.length()-1) == '-') &&
                    ((output.length() > 1) &&
                            (OPERATORS.indexOf(output.charAt(output.length()-2)) == -1))) {
                output.setCharAt(output.length()-1,'+');
            }
            else if (output.charAt(output.length()-1) == '+') {
                output.setCharAt(output.length()-1,'-');
            }
            else if ((OPERATORS.indexOf(output.charAt(output.length()-1)) > -1) &&
                    !(output.charAt(output.length()-1) == '-')) {
                output.append(buttonText);
                negativeNumbersIndexes.add(output.length()-1);
            }
            else if (output.charAt(output.length()-1) == DOT) {
                output.setCharAt(output.length()-1,buttonText.charAt(0));
                isLoneZero = false;
                isDot = false;
            }
            if (startNewOutput &&  output.charAt(0) == '-') {
                negativeNumbersIndexes.add(0);
            }
            startNewOutput = false;
        }
        else if (buttonText.equals("*")) {
            Log.e(TAG,"buttonClick : "+buttonText);
            if (output.length() == 0) {
                //do nothing
            }
            else if (DIGITS.indexOf(output.charAt(output.length()-1)) > -1) {
                output.append(buttonText);
                isLoneZero = false;
                isDot = false;
            }
            else if (output.charAt(output.length()-1) == DOT) {
                output.setCharAt(output.length()-1,buttonText.charAt(0));
                isLoneZero = false;
                isDot = false;
            }
            else if ((output.charAt(output.length()-1) == '-') && output.length() ==1 ) {
                //do nothing
            }
            else if ((output.charAt(output.length()-1) == '-') && (output.length() > 1) &&
                    (OPERATORS.indexOf(output.charAt(output.length()-2)) > -1)) {
                output.deleteCharAt(output.length()-1);
                output.setCharAt(output.length()-1,buttonText.charAt(0));
            }
            else if ((OPERATORS.indexOf(output.charAt(output.length()-1)) > -1)) {
                output.setCharAt(output.length()-1,buttonText.charAt(0));
            }
            if (startNewOutput &&  output.charAt(0) == '-') {
                negativeNumbersIndexes.add(0);
            }
            startNewOutput = false;
        }
        else if (buttonText.equals("/")) {
            Log.e(TAG,"buttonClick : "+buttonText);
            if (output.length() == 0) {
                //do nothing
            }
            else if (DIGITS.indexOf(output.charAt(output.length()-1)) > -1) {
                output.append(buttonText);
                isLoneZero = false;
                isDot = false;
            }
            else if (output.charAt(output.length()-1) == DOT) {
                output.setCharAt(output.length()-1,buttonText.charAt(0));
                isLoneZero = false;
                isDot = false;
            }
            else if ((output.charAt(output.length()-1) == '-') && output.length() ==1 ) {
                //do nothing
            }
            else if ((output.charAt(output.length()-1) == '-') && (output.length() > 1) &&
                    (OPERATORS.indexOf(output.charAt(output.length()-2)) > -1)) {
                output.deleteCharAt(output.length()-1);
                output.setCharAt(output.length()-1,buttonText.charAt(0));
            }
            else if ((OPERATORS.indexOf(output.charAt(output.length()-1)) > -1)) {
                output.setCharAt(output.length()-1,buttonText.charAt(0));
            }
            if (startNewOutput &&  output.charAt(0) == '-') {
                negativeNumbersIndexes.add(0);
            }
            startNewOutput = false;
        }
        else if (buttonText.equals(DOT.toString())) {
            Log.e(TAG,"buttonClick : "+buttonText);
            if (output.length() == 0) {
                //do nothing
            }
            else if (isDot) {
                //do nothing
            }
            else if (DIGITS.indexOf(output.charAt(output.length()-1)) > -1) {
                output.append(buttonText);
                isDot = true;
            }
        }
        outcomeTextView.setText(output);
    }

    private StringBuilder replaceRange(StringBuilder originalString, int startIndex, int endIndex, String substitute) {
        return (new StringBuilder(originalString.substring(0, startIndex) + substitute + originalString.substring(endIndex)));
    }
    private String calculateSingleOperator(String expression, Character operator) {
        Log.e(TAG,"calculateSingleOperator : "+operator.toString());
        String[] numbers = expression.split(Pattern.quote(operator.toString()));
        StringBuilder firstNumberStringBuilder = new StringBuilder(numbers[0]);
        StringBuilder secondNumberStringBuilder = new StringBuilder(numbers[1]);
        StringBuilder resultStringBuilder = new StringBuilder();
        Double firstNumber;
        Double secondNumber;
        Double result = 0.0;
        Log.e(TAG,"calculateSingleOperator : "+expression+" first="+firstNumberStringBuilder+" second="+secondNumberStringBuilder);
        if (firstNumberStringBuilder.indexOf("$") > -1) {
            firstNumberStringBuilder.setCharAt(firstNumberStringBuilder.indexOf("$"),'-');
        }
        if (secondNumberStringBuilder.indexOf("$") > -1) {
            secondNumberStringBuilder.setCharAt(secondNumberStringBuilder.indexOf("$"),'-');
        }
        Log.e(TAG,"calculateSingleOperator : "+expression+" first="+firstNumberStringBuilder+" second="+secondNumberStringBuilder);
        try {
            firstNumber = Double.parseDouble(firstNumberStringBuilder.toString());
            secondNumber = Double.parseDouble(secondNumberStringBuilder.toString());
        } catch (NumberFormatException e) {
            return "Błąd";
        }
        if (operator.equals('-')) {
            result = firstNumber - secondNumber;
        } else if (operator.equals('+')) {
            result = firstNumber + secondNumber;
        } else if (operator.equals('*')) {
            result = firstNumber * secondNumber;
        } else if (operator.equals('/')) {
            result = firstNumber / secondNumber;
        }
        resultStringBuilder.append(result.toString());
        if (resultStringBuilder.indexOf("-") > -1) {
            resultStringBuilder.setCharAt(resultStringBuilder.indexOf("-"),'$');
        }
        Log.e(TAG,"calculateSingleOperator : "+resultStringBuilder.toString());
        return resultStringBuilder.toString();
    }
    private StringBuilder calculateResult(StringBuilder expression) {
        StringBuilder result = new StringBuilder(expression);
        for (Integer index:negativeNumbersIndexes) {
            result.setCharAt(index,'$');
        }
        Log.e(TAG,"calculateResult : "+"poczatek "+result);
        while ((result.lastIndexOf("*") > -1) || (result.lastIndexOf("/") > -1)) {
            Log.e(TAG,"calculateResult : "+" while */");
            int multiplicationIndex = result.indexOf("*");
            int divisionIndex = result.indexOf("/");
            int operationIndex;
            Character operator;
            int replaceStartIndex = -1;
            int replaceStopIndex = -1;
            int loopIndex;
            if ((multiplicationIndex > -1) && (divisionIndex == -1)) {
                operationIndex = multiplicationIndex;
            }
            else if ((multiplicationIndex == -1) && (divisionIndex > -1)) {
                operationIndex = divisionIndex;
            }
            else if (multiplicationIndex < divisionIndex) {
                operationIndex = multiplicationIndex;
            } else {
                operationIndex = divisionIndex;
            }
            operator = result.charAt(operationIndex);
            loopIndex = operationIndex -1;
            while(loopIndex > -1) {
                if(result.charAt(loopIndex) == '-' && loopIndex != 0
                        && OPERATORS.indexOf(result.charAt(loopIndex-1)) > -1) {
                    replaceStartIndex = loopIndex;
                    break;
                }
                else if (OPERATORS.indexOf(result.charAt(loopIndex)) > -1) {
                    replaceStartIndex = loopIndex+1;
                    break;
                } else if (loopIndex == 0) {
                    replaceStartIndex = loopIndex;
                    break;
                }
                loopIndex--;
            }
            loopIndex = operationIndex + 1;
            while (loopIndex < result.length()) {
                if (OPERATORS.indexOf(result.charAt(loopIndex)) > -1) {
                    replaceStopIndex = loopIndex-1;
                    break;
                }
                else if (loopIndex == result.length()-1) {
                    replaceStopIndex = loopIndex;
                    break;
                }
                loopIndex++;
            }
            String substitute = calculateSingleOperator(result.substring(replaceStartIndex,replaceStopIndex+1),operator);
            result = replaceRange(result, replaceStartIndex,replaceStopIndex+1,substitute);
            Log.e(TAG,"calculateResult : "+" while */ "+result.toString());
        }
        while ((result.lastIndexOf("+") > -1) || (result.lastIndexOf("-") >-1)) {
            Log.e(TAG,"calculateResult : "+" while +-");
            int additionIndex = result.indexOf("+");
            int subtractionIndex = result.indexOf("-");
            int operationIndex;
            Character operator;
            int replaceStartIndex = -1;
            int replaceStopIndex = -1;
            int loopIndex;
            if ((additionIndex > -1) && (subtractionIndex == -1)) {
                operationIndex = additionIndex;
            }
            else if ((additionIndex == -1) && (subtractionIndex > -1)) {
                operationIndex = subtractionIndex;
            }
            else if (additionIndex < subtractionIndex) {
                operationIndex = additionIndex;
            } else {
                operationIndex = subtractionIndex;
            }
            operator = result.charAt(operationIndex);
            loopIndex = operationIndex -1;
            while(loopIndex > -1) {
                Log.e(TAG,"calculateResult : "+" while loop <-");
                if(result.charAt(loopIndex) == '-' && loopIndex != 0
                        && OPERATORS.indexOf(result.charAt(loopIndex-1)) > -1) {
                    replaceStartIndex = loopIndex;
                    break;
                }
                else if (OPERATORS.indexOf(result.charAt(loopIndex)) > -1) {
                    replaceStartIndex = loopIndex+1;
                    break;
                } else if (loopIndex == 0) {
                    replaceStartIndex = loopIndex;
                    break;
                }
                loopIndex--;
            }
            loopIndex = operationIndex + 1;
            while (loopIndex < result.length()) {
                Log.e(TAG,"calculateResult : "+" while loop ->");
                if (OPERATORS.indexOf(result.charAt(loopIndex)) > -1) {
                    replaceStopIndex = loopIndex-1;
                    break;
                }
                else if (loopIndex == result.length()-1) {
                    replaceStopIndex = loopIndex;
                    break;
                }
                loopIndex++;
            }
            String substitute = calculateSingleOperator(result.substring(replaceStartIndex,replaceStopIndex+1),operator);
            result = replaceRange(result, replaceStartIndex,replaceStopIndex+1,substitute);
            Log.e(TAG,"calculateResult : "+" while +- "+result.toString());
        }
        while (result.indexOf("$") >-1) {
            result.setCharAt(result.indexOf("$"),'-');
        }
        Log.e(TAG,"calculateResult : "+" return "+result);
        return result;
    }
    public void historyClick(View view) {
        Intent intent = new Intent(this,HistoryActivity.class);
        intent.putExtra(EXTRA_MESSAGE,history);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    history.clear();
                }
                break;
            }
            default: {
                break;
            }
        }
    }
}
