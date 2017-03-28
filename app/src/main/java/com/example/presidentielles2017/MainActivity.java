package com.example.presidentielles2017;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static java.lang.Math.exp;

public class MainActivity extends AppCompatActivity {
    public static final String CONFUSION_MATRIX = "com.example.presidentielles2017.CONFUSION_MATRIX";
    private Random randomGenerator = new Random();
    List allPropositions = new ArrayList();
    int currentIndex;
    boolean userHasClicked = false;
    String[] candidates;
    int totalClicks = 0;
    int correctClicks = 0;
    int [][] confusionMatrix;
    HashMap counter = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load the CSV for each candidate
        InputStreamReader is = null;
        try {
            String[] filenames = { "benoit_hamon.csv", "emmanuel_macron.csv",
                    "francois_fillon.csv","jean_luc_melenchon.csv", "marine_le_pen.csv",
                     "nicolas_dupont_aignan.csv"};
            candidates = new String[]{getResources().getString(R.string.candidate_BH),
                    getResources().getString(R.string.candidate_EM),
                    getResources().getString(R.string.candidate_FF),
                    getResources().getString(R.string.candidate_JLM),
                    getResources().getString(R.string.candidate_MLP),
                    getResources().getString(R.string.candidate_NDA)};
            for (String candidate: candidates) {
                counter.put(candidate, 0);
            }

            confusionMatrix = new int[candidates.length][candidates.length];
            for (int i=0; i<candidates.length;i++){
                for (int j=0; j<candidates.length; j++){
                    confusionMatrix[i][j] = 0;
                }
            }
            for (int i = 0; i < filenames.length; i++) {
                String filename =  filenames[i];
                String candidate = candidates[i];
                is = new InputStreamReader(getAssets()
                        .open(filename));
                BufferedReader reader = new BufferedReader(is);
                reader.readLine();

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] RowData = line.split("\",\"");
                    List<String> terms = new ArrayList<String>();

                    terms.add(RowData[0].substring(1));
                    terms.add(RowData[1].substring(0, RowData[1].length() - 1));
                    terms.add(candidate);
                    allPropositions.add(terms);

                }
                reader.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        // populate the first question
        makeNewQuestion(this.findViewById(android.R.id.content));

        // update score to 0
        updateScore();


    }

    // selects a new question and displays it
    public List<String> generateNewQuestion() {
        double[] weights = new double[candidates.length];
        int i = 0;
        for (String candidate: candidates) {
            //TODO : weights[i] = exp((double) counter.get(candidate));
            i ++;
        }
        int index = randomGenerator.nextInt(allPropositions.size());
        currentIndex = index;
        List<String> terms = (List<String>) allPropositions.get(index);
        return terms;
    }

    public void makeNewQuestion(View view) {
        // get a new question data and display it
        List<String> newQuestion = generateNewQuestion();
        TextView textView = (TextView) findViewById(R.id.textViewProposition);
        textView.setText(newQuestion.get(0));
        String candidate = newQuestion.get(2);
        counter.put(candidate, 1 + (int) counter.get(candidate));
        resetButtonColors();
        userHasClicked = false;

    }

    public void candidateClick(View view) {
        // validates if the user has clicked the correct button
        if (! userHasClicked) {
            Button b = ((Button) view);

            String clickedCandidate = (String) b.getText();
            int clickedIndex = Arrays.asList(candidates).indexOf(clickedCandidate);

            List<String> terms = (List<String>) allPropositions.get(currentIndex);
            String correctCandidate = terms.get(2);
            int correctIndex = Arrays.asList(candidates).indexOf(correctCandidate);

            if (clickedCandidate.equals(correctCandidate)) {
                b.setBackgroundColor(0xff00ff00);
                correctClicks += 1;
            }
            else
            {
                b.setBackgroundColor(0xffff0000);
                int[] ids = {R.id.buttonBH, R.id.buttonEM, R.id.buttonFF,
                        R.id.buttonJLM, R.id.buttonMLP, R.id.buttonNDA};
                for (int id : ids) {
                    final Button button = (Button) findViewById(id);
                    if (button.getText().equals(correctCandidate)){
                        button.setBackgroundColor(0xff00ff00);
                        break;
                    }
                }
            }

            totalClicks += 1;
            updateScore();
            updateConfusionMatrix(clickedIndex, correctIndex);
        }

        userHasClicked = true;

    }

    private void updateScore() {
        TextView textView = (TextView) findViewById(R.id.textViewScore);
        textView.setText(String.format("%d / %d", correctClicks, totalClicks));
    }

    public void resetButtonColors() {
        int[] ids = {R.id.buttonBH, R.id.buttonEM, R.id.buttonFF,
            R.id.buttonJLM, R.id.buttonMLP, R.id.buttonNDA};
        for (int id : ids) {
            final Button button = (Button) findViewById(id);
            button.setBackgroundResource(android.R.drawable.btn_default);
        }

    }

    public void updateConfusionMatrix(int clickedCandidate, int correctCandidate) {
        // updates the confusion matrix
        confusionMatrix[correctCandidate][clickedCandidate] += 1;
    }

    public void displayConfusionMatrix(View view) {
        Intent intent = new Intent(this, DisplayConfusionMatrixActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFUSION_MATRIX, confusionMatrix);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}


