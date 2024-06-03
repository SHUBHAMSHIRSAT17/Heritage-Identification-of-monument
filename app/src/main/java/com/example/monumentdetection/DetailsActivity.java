package com.example.monumentdetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    ImageView imageView;
    Bitmap bitmap;
    TextView monumentName1, monumentName2, monumentName3;
    TextView monumentConfidence1, monumentConfidence2, monumentConfidence3;
    TextView monumentDescription1, monumentDescription2, monumentDescription3;
    TextView monumentRead1, monumentRead2, monumentRead3;
    ImageButton monumentTTS1, monumentTTS2, monumentTTS3;
    TextToSpeech textToSpeech;
    String continent, model, desc;
    Boolean translate;
    String mName1, mName2, mName3;

    FirebaseTranslator englishHindiTranslator;

    @SuppressLint({"WrongThread", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        imageView = findViewById(R.id.details_image);

        monumentName1 = findViewById(R.id.monument_name1);
        monumentName2 = findViewById(R.id.monument_name2);
        monumentName3 = findViewById(R.id.monument_name3);

        monumentConfidence1 = findViewById(R.id.monument_confidence1);
        monumentConfidence2 = findViewById(R.id.monument_confidence2);
        monumentConfidence3 = findViewById(R.id.monument_confidence3);

        monumentDescription1 = findViewById(R.id.monument_description1);
        monumentDescription2 = findViewById(R.id.monument_description2);
        monumentDescription3 = findViewById(R.id.monument_description3);

        monumentRead1 = findViewById(R.id.monument_read1);
        monumentRead2 = findViewById(R.id.monument_read2);
        monumentRead3 = findViewById(R.id.monument_read3);

        monumentTTS1 = findViewById(R.id.monument_tts1);
        monumentTTS2 = findViewById(R.id.monument_tts2);
        monumentTTS3 = findViewById(R.id.monument_tts3);

        // downloading translation model
        downloadModal();

        // getting intent data
        gettingIntentData();

        // detecting monument
        monumentDetection();

        monumentRead1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, mName1);
                startActivity(intent);
            }
        });

        monumentRead2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, mName2);
                startActivity(intent);
            }
        });

        monumentRead3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, mName3);
                startActivity(intent);
            }
        });


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                if(i!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        monumentTTS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(monumentDescription1.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        monumentTTS2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(monumentDescription2.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        monumentTTS3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(monumentDescription3.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });

    }

    private void gettingIntentData() {

        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        translate = preferences.getBoolean("translate", false);

        Bundle bundle = getIntent().getExtras();
        continent = bundle.getString("continent");
        bitmap = bundle.getParcelable("bitmap");
        imageView.setImageBitmap(bitmap);

        if (continent.equals(getResources().getString(R.string.asia))) {
            continent = "asia";
        } else if (continent.equals(getResources().getString(R.string.africa))) {
            continent = "africa";
        } else if (continent.equals(getResources().getString(R.string.europe))) {
            continent = "europe";
        } else if (continent.equals(getResources().getString(R.string.north_america))) {
            continent = "namerica";
        } else if (continent.equals(getResources().getString(R.string.south_america))) {
            continent = "samerica";
        } else if (continent.equals(getResources().getString(R.string.australia))) {
            continent = "australia";
        }
    }

    private void monumentDetection() {
        model = continent + ".tflite";

        LocalModel localModel = new LocalModel.Builder().setAssetFilePath(model).build();

        CustomObjectDetectorOptions customObjectDetectorOptions =
                new CustomObjectDetectorOptions.Builder(localModel)
                        .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableClassification()
                        .setClassificationConfidenceThreshold(0.5f)
                        .setMaxPerObjectLabelCount(3)
                        .build();

        ObjectDetector objectDetector = ObjectDetection.getClient(customObjectDetectorOptions);

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        objectDetector
                .process(image)
                .addOnSuccessListener(results -> {
                    for (DetectedObject detectedObject : results) {
                        int count = 1;
                        for (DetectedObject.Label label : detectedObject.getLabels()) {
                            if (count == 1) {
                                mName1 = label.getText();
                                monumentName1.setText(mName1);
                                if (translate)
                                    translateName1(mName1);
                                monumentConfidence1.setText(String.valueOf(label.getConfidence()*100).substring(0, 2) + " %");
                                getDesc(label.getText(), 1);
                            } else if (count == 2) {
                                mName2 = label.getText();
                                monumentName2.setText(mName2);
                                if (translate)
                                    translateName2(mName2);
                                monumentConfidence2.setText(String.valueOf(label.getConfidence()*100).substring(0, 2) + " %");
                                getDesc(label.getText(), 2);
                            } else {
                                mName3 = label.getText();
                                monumentName3.setText(mName3);
                                if (translate)
                                    translateName3(mName3);
                                monumentConfidence3.setText(String.valueOf(label.getConfidence()*100).substring(0, 2) + " %");
                                getDesc(label.getText(), 3);
                            }
                            count++;
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailsActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDesc(String title, int id) {

        String queryTitle = title.replace("Gateway Of India Mumbai", "Gateway of India").replace(" ", "%20").replace("-", "%20");

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&exsentences=1&explaintext=&titles=" + queryTitle , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
//                    Toast.makeText(DetailsActivity.this, "" + response.getString("query"), Toast.LENGTH_SHORT).show();
                    int i = response.getJSONObject("query").getJSONObject("pages").toString().indexOf("extract");
                    int j = response.getJSONObject("query").getJSONObject("pages").toString().indexOf("\"}}");
                    Log.d("myapp", "" + response.getJSONObject("query").getJSONObject("pages").toString().substring(i+10, j));
                    if (id == 1) {
                        desc = response.getJSONObject("query").getJSONObject("pages").toString().substring(i+10, j);
                        monumentDescription1.setText(desc);
                        if (translate)
                            translateDesc1(desc);
                    } else if (id == 2) {
                        desc = response.getJSONObject("query").getJSONObject("pages").toString().substring(i+10, j);
                        monumentDescription2.setText(desc);
                        if (translate)
                            translateDesc2(desc);
                    } else if (id == 3) {
                        desc = response.getJSONObject("query").getJSONObject("pages").toString().substring(i+10, j);
                        monumentDescription3.setText(desc);
                        if (translate)
                            translateDesc3(desc);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });

        requestQueue.add(jsonObjectRequest);

    }


    private void downloadModal() {

        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                                                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                                .build();

        englishHindiTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();

        englishHindiTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DetailsActivity.this, "Please wait language modal is being downloaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivity.this, "Fail to download modal", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void translateName1(String input) {
        englishHindiTranslator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                monumentName1.setText(s);
                mName1 = s;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivity.this, "Failed to translate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void translateName2(String input) {
        englishHindiTranslator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                monumentName2.setText(s);
                mName2 = s;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivity.this, "Failed to translate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void translateName3(String input) {
        englishHindiTranslator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                monumentName3.setText(s);
                mName3 = s;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivity.this, "Failed to translate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void translateDesc1(String input) {
        englishHindiTranslator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                monumentDescription1.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivity.this, "Failed to translate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void translateDesc2(String input) {
        englishHindiTranslator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                monumentDescription2.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivity.this, "Failed to translate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void translateDesc3(String input) {
        englishHindiTranslator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                monumentDescription3.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailsActivity.this, "Failed to translate", Toast.LENGTH_SHORT).show();
            }
        });
    }

}