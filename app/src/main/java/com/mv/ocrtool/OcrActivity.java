package com.mv.ocrtool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class OcrActivity extends AppCompatActivity {

    private TextView detectedTextView;
    private String m_chosenDir;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        detectedTextView = (TextView) findViewById(R.id.detected_text);
        detectedTextView.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        String detectedText = intent.getExtras().getString("detectText");
        detectedTextView.setText(detectedText);
    }


    public void write(View view) {
        m_chosenDir = "";
        boolean m_newFolderEnabled = true;
        DirectoryChooserDialog directoryChooserDialog =
                new DirectoryChooserDialog(OcrActivity.this,
                        new DirectoryChooserDialog.ChosenDirectoryListener()
                        {
                            @Override
                            public void onChosenDir(String chosenDir)
                            {
                                m_chosenDir = chosenDir;
                                Toast.makeText(
                                        OcrActivity.this, "Chosen directory: " +
                                                m_chosenDir, Toast.LENGTH_LONG).show();
                                LayoutInflater li = LayoutInflater.from(OcrActivity.this);
                                View promptsView = li.inflate(R.layout.prompt, null);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        OcrActivity.this);

                                // set prompts.xml to alertdialog builder
                                alertDialogBuilder.setView(promptsView);

                                final EditText userInput = (EditText) promptsView
                                        .findViewById(R.id.editTextDialogUserInput);

                                // set dialog message
                                alertDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        // get user input and set it to result
                                                        // edit text
                                                        fileName = userInput.getText().toString();
                                                        try {
                                                            String Mytextmessage  = detectedTextView.getText().toString();
                                                            FileOutputStream fileOutputStream = new FileOutputStream( m_chosenDir + "/" +fileName +".txt");
                                                            fileOutputStream.write(Mytextmessage.getBytes());
                                                            fileOutputStream.close();
                                                            Toast.makeText(getApplicationContext(),"Text Saved",Toast.LENGTH_LONG).show();
                                                            detectedTextView.setText("");
                                                        } catch (FileNotFoundException e) {
                                                            e.printStackTrace();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        } finally {
                                                            Toast.makeText(getApplicationContext(), "WOrks?", Toast.LENGTH_LONG);
                                                        }

                                                    }
                                                })
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();


                            }
                        });
        // Toggle new folder button enabling
        directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
        // Load directory chooser dialog for initial 'm_chosenDir' directory.
        // The registered callback will be called upon final directory selection.
        directoryChooserDialog.chooseDirectory(m_chosenDir);
        m_newFolderEnabled = ! m_newFolderEnabled;
    }

    public interface ChosenDirectoryListener {
        public void onChosenDir(String chosenDir);
    }
}
