package com.example.phasmatic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phasmatic.R;
import com.example.phasmatic.data.model.UserExpectation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireActivity extends AppCompatActivity {

    private TextView txtProgress, txtQuestion;
    private EditText edtAnswer;
    private Button btnPrev, btnNext;
    private ProgressBar progressQuestions;
    private TextView txtModeTitle;
    private ImageView[] stepDots;

    private ImageButton btnBack;
    private ImageView imgProfile;
    private ProfileMenuHelper profileMenuHelper;
    private BackButtonHelper backButtonHelper;


    private String userId, userFullName, userEmail, userPhone, modeType;

    private List<String> questions = new ArrayList<>();
    private List<String> answers = new ArrayList<>();
    private int currentIndex = 0;

    private DatabaseReference expectationsRef;
    private DatabaseReference questionsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        txtModeTitle = findViewById(R.id.txtModeTitle);
        progressQuestions = findViewById(R.id.progressQuestions);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userFullName = intent.getStringExtra("userFullName");
        userEmail = intent.getStringExtra("userEmail");
        userPhone = intent.getStringExtra("userPhone");
        modeType = intent.getStringExtra("modeType");

        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);

        BackButtonHelper.attachToGoModeSelection(
                this,
                R.id.btnBack,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        profileMenuHelper = new ProfileMenuHelper(
                this,
                userId,
                userFullName,
                userEmail,
                userPhone
        );

        imgProfile.setOnClickListener(v -> profileMenuHelper.showProfileMenu(v));



        ImageView step1 = findViewById(R.id.step1);
        ImageView step2 = findViewById(R.id.step2);
        ImageView step3 = findViewById(R.id.step3);
        ImageView step4 = findViewById(R.id.step4);
        ImageView step5 = findViewById(R.id.step5);

        stepDots = new ImageView[]{step1, step2, step3, step4, step5};

        if ("erasmus".equals(modeType)) {
            txtModeTitle.setText("Erasmus questionnaire");
        } else if ("master".equals(modeType)) {
            txtModeTitle.setText("Master questionnaire");
        } else { // AKOMA DEN TO EXW UPOLOGISEI
            txtModeTitle.setText("Study advisor questionnaire");
        }

        for (int i = 0; i < stepDots.length; i++) {
            final int index = i;
            stepDots[i].setOnClickListener(v -> {
                if (index < questions.size()) {
                    saveCurrentAnswer();
                    currentIndex = index;
                    updateUI();
                }
            });
        }

        txtProgress = findViewById(R.id.txtProgress);
        txtQuestion = findViewById(R.id.txtQuestion);
        edtAnswer = findViewById(R.id.edtAnswer);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://mega-5a5b4-default-rtdb.europe-west1.firebasedatabase.app"
        );
        expectationsRef = db.getReference("user_expectations");

        questionsRef = db.getReference("questionnaire_questions");


        loadQuestionsFromDb(modeType);

        btnPrev.setOnClickListener(v -> {
            saveCurrentAnswer();
            if (currentIndex > 0) {
                currentIndex--;
                updateUI();
            }
        });

        btnNext.setOnClickListener(v -> {
            saveCurrentAnswer();
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                updateUI();
            } else {
                saveExpectationsAndGoChat();
            }
        });
    }

    private void loadQuestionsFromDb(String mode) {
        questions.clear();
        answers.clear();

        questionsRef.orderByChild("mode_type")
                .equalTo(mode)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {

                        //H QItem KRATAEI TH SEIRA THS ERWTHSHS (question_id) KAI TO TEXT THS
                        class QItem {
                            public long order;
                            public String text;
                            public QItem(long order, String text) {
                                this.order = order;
                                this.text = text;
                            }
                        }
                        List<QItem> tmp = new ArrayList<>();

                        for (com.google.firebase.database.DataSnapshot child : snapshot.getChildren()) {
                            //METATROPH GIATI H IS_ACTIVE PREPEI NA PAREI NUMBER KAI OXI BOOLEAN
                            Object activeObj = child.child("is_active").getValue();
                            boolean active = true;

                            if (activeObj instanceof Boolean) {
                                active = (Boolean) activeObj;
                            } else if (activeObj instanceof Long) {
                                active = ((Long) activeObj) != 0;
                            }

                            if (!active) return;


                            Long order = child.child("question_id").getValue(Long.class);
                            String q = child.child("question").getValue(String.class);
                            if (order != null && q != null && !q.trim().isEmpty()) {
                                tmp.add(new QItem(order, q));
                            }
                        }

                        //TAJINOMHSH tou tmp
                        int n = tmp.size();
                        for (int i = 0; i < n - 1; i++) {
                            for (int j = 0; j < n - 1 - i; j++) {
                                if (tmp.get(j).order > tmp.get(j + 1).order) {
                                    QItem t = tmp.get(j);
                                    tmp.set(j, tmp.get(j + 1));
                                    tmp.set(j + 1, t);
                                }
                            }
                        }

                        for (QItem qi : tmp) {
                            questions.add(qi.text);
                            answers.add("");
                        }

                        currentIndex = 0;

                        if (questions.isEmpty()) {
                            Toast.makeText(QuestionnaireActivity.this,
                                    "No questions configured for " + mode,
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            updateUI();
                        }
                    }

                    @Override
                    public void onCancelled(com.google.firebase.database.DatabaseError error) {
                        Toast.makeText(QuestionnaireActivity.this,
                                "Failed to load questions: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


    private void updateUI() {
        txtQuestion.setText(questions.get(currentIndex));
        txtProgress.setText((currentIndex + 1) + " / " + questions.size());

        progressQuestions.setMax(questions.size());
        progressQuestions.setProgress(currentIndex + 1);

        edtAnswer.setText(answers.get(currentIndex));

        btnPrev.setEnabled(currentIndex > 0);
        btnNext.setText(currentIndex == questions.size() - 1 ? "Finish" : "Next");

        for (int i = 0; i < stepDots.length; i++) {
            if (i < questions.size()) {
                stepDots[i].setVisibility(View.VISIBLE);
                stepDots[i].setImageResource(
                        i == currentIndex ? R.drawable.ic_step_active : R.drawable.ic_step_inactive
                );
            } else {
                stepDots[i].setVisibility(View.GONE);
            }
        }
    }


    private void saveCurrentAnswer() {
        String ans = edtAnswer.getText().toString().trim();
        answers.set(currentIndex, ans);
    }

    private void saveExpectationsAndGoChat() {

        StringBuilder sb = new StringBuilder();

        if ("erasmus".equals(modeType)) {
            sb.append("Ψάχνω για πρόγραμμα Erasmus που να ταιριάζει σε μένα. ");
        } else if ("master".equals(modeType)) {
            sb.append("Ψάχνω για πρόγραμμα μεταπτυχιακού (Master) που να ταιριάζει σε μένα. ");
        } else { //AUTH H PERIPTWSH DEN KALIPTETAI AKOMA
            sb.append("Ψάχνω για συμβουλές που να μου ταιριάζουν. ");
        }

        sb.append("Με βάση τα παρακάτω στοιχεία θέλω να μου προτείνεις τις πιο κατάλληλες επιλογές:\n\n");

        for (int i = 0; i < questions.size(); i++) {
            if (!answers.get(i).isEmpty()) {
                sb.append("- ")
                        .append(questions.get(i))
                        .append(" Απάντηση: ")
                        .append(answers.get(i))
                        .append("\n");
            }
        }

        if ("erasmus".equals(modeType)) {
            sb.append("\nΛάβε υπόψη όλα τα παραπάνω και πρότεινέ μου 2-3 κατάλληλες επιλογές Erasmus, εξηγώντας γιατί ταιριάζουν στο προφίλ μου.");
        } else if ("master".equals(modeType)) {
            sb.append("\nΛάβε υπόψη όλα τα παραπάνω και πρότεινέ μου 2-3 κατάλληλα προγράμματα master, εξηγώντας γιατί ταιριάζουν στο προφίλ μου.");
        } else {
            sb.append("\nΛάβε υπόψη όλα τα παραπάνω και δώσε μου καθοδήγηση για τα επόμενα βήματα στις σπουδές μου.");
        }

        String expectationsText = sb.toString().trim();

        String id = expectationsRef.push().getKey();
        if (id == null) {
            Toast.makeText(this, "Failed to create expectation id", Toast.LENGTH_SHORT).show();
            return;
        }

        UserExpectation expectation = new UserExpectation(
                id,
                userId,
                modeType,
                expectationsText
        );

        expectationsRef.child(id)
                .setValue(expectation)
                .addOnSuccessListener(unused -> {
                    Intent i;
                    if ("erasmus".equals(modeType)) {
                        i = new Intent(QuestionnaireActivity.this, ErasmusChatActivity.class);
                    } else {
                        i = new Intent(QuestionnaireActivity.this, MasterChatActivity.class);
                    }
                    i.putExtra("userId", userId);
                    i.putExtra("userFullName", userFullName);
                    i.putExtra("userEmail", userEmail);
                    i.putExtra("userPhone", userPhone);
                    i.putExtra("userExpectations", expectationsText);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}

