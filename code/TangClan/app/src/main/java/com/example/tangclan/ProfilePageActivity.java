package com.example.tangclan;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tangclan.R;
import com.example.tangclan.WizVIew;

public class ProfilePageActivity extends AppCompatActivity {

    private WizVIew wizVIew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Your profile page layout

        wizVIew = new ViewModelProvider(this).get(WizVIew.class);

        // Retrieve data from ViewModel
        String emotionalState = wizVIew.getEmotionalState();
        String reason = wizVIew.getReason();
        String socialSituation = wizVIew.getSocialSituation();
        Bitmap optionalPicture = wizVIew.getOptionalPicture();

        // Display the data in your UI elements
        TextView reasonTextView = findViewById(R.id.textViewReason);
        TextView situationTextView = findViewById(R.id.textViewSocialSituation);
        ImageView emoticonImageView = findViewById(R.id.emoticonImageView);
        ImageView pictureImageView = findViewById(R.id.pictureImageView);  // ImageView to display the optional picture

        // Set text for reason and social situation
        reasonTextView.setText(reason);
        situationTextView.setText(socialSituation);

        // Display emoticon based on emotional state
        if ("happy".equals(emotionalState)) {
            emoticonImageView.setImageResource(R.drawable.ic_emotion_happy); // Replace with your emoticon resource
        } else if ("sad".equals(emotionalState)) {
            emoticonImageView.setImageResource(R.drawable.ic_emotion_sad);  // Replace with your emoticon resource
        } else if ("angry".equals(emotionalState)){
            emoticonImageView.setImageResource(R.drawable.ic_emotion_angry);
        } else if ("anxious".equals(emotionalState)){
            emoticonImageView.setImageResource(R.drawable.ic_emotion_anxious);
        } else if ("ashasmed".equals(emotionalState)){
            emoticonImageView.setImageResource(R.drawable.ic_emotion_ashamed);
        } else if ("calm".equals(emotionalState)){
            emoticonImageView.setImageResource(R.drawable.ic_emotion_calm);
        } else if ("confused".equals(emotionalState)){
            emoticonImageView.setImageResource(R.drawable.ic_emotion_confused);
        } else if ("disgusted".equals(emotionalState)){
            emoticonImageView.setImageResource(R.drawable.ic_emotion_disgusted);
        } else if ("no idea".equals(emotionalState)){
            emoticonImageView.setImageResource(R.drawable.ic_emotion_no_idea);
        } else if ("surprised".equals(emotionalState)){
            emoticonImageView.setImageResource(R.drawable.ic_emotion_surprised);
        } else if ("terrified".equals(emotionalState)){
            emoticonImageView.setImageResource(R.drawable.ic_emotion_terrified);
        }

        // Display optional picture if it exists
        if (optionalPicture != null) {
            pictureImageView.setImageBitmap(optionalPicture);
        }
    }
}
