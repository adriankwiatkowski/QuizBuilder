package com.example.android.quizbuilder.utilities;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.android.quizbuilder.R;
import com.example.android.quizbuilder.data.database.QuizEntry;
import com.example.android.quizbuilder.data.database.QuizPage;

import java.util.List;

import static com.example.android.quizbuilder.data.database.QuizConstants.TYPE_TEXT_ANSWER;

public class QuizUtils {

    public static String getEndScoreMessage(Context context, int score, int maxScore, String userName) {
        double percentageScore = ((double) score * 100) / (double) maxScore;
        String endScoreMessage;
        if (percentageScore >= 100)
            endScoreMessage = context.getString(R.string.score_max, userName);
        else if (percentageScore >= 80)
            endScoreMessage = context.getString(R.string.score_high, userName);
        else if (percentageScore >= 60)
            endScoreMessage = context.getString(R.string.score_good, userName);
        else if (percentageScore >= 40)
            endScoreMessage = context.getString(R.string.score_medium, userName);
        else if (percentageScore >= 20)
            endScoreMessage = context.getString(R.string.score_low, userName);
        else if (percentageScore > 0)
            endScoreMessage = context.getString(R.string.score_very_low, userName);
        else endScoreMessage = context.getString(R.string.score_0, userName);
        return endScoreMessage;
    }

    public static boolean canPlay(Context context, @Nullable QuizEntry quizEntry) {
        boolean canPlay = true;
        if (quizEntry == null) {
            canPlay = false;
        } else if (quizEntry.getPages().size() <= 0) {
            canPlay = false;
            makeToast(context, context.getString(R.string.error_no_questions));
        } else {
            for (int i = 0; i < quizEntry.getPages().size(); i++) {
                if (!canPlay) {
                    break;
                }
                QuizPage quizPage = quizEntry.getPages().get(i);

                int page = i + 1;

                if (TextUtils.isEmpty(quizPage.getQuestion())) {
                    canPlay = false;
                    makeToast(context, context.getString(R.string.error_no_question, page));
                    break;
                }
                if (quizPage.getType() != TYPE_TEXT_ANSWER) {
                    List<String> answers = quizPage.getAnswers();
                    for (int j = 0; j < answers.size(); j++) {
                        if (TextUtils.isEmpty(answers.get(j))) {
                            canPlay = false;
                            makeToast(context, context.getString(R.string.error_no_answers, page));
                            break;
                        }
                    }
                }
                List<String> correctAnswers = quizPage.getCorrectAnswers();
                if (correctAnswers == null || correctAnswers.isEmpty()) {
                    canPlay = false;
                    makeToast(context, context.getString(R.string.error_no_correct_answer, page));
                    break;
                }
            }
        }
        return canPlay;
    }

    private static void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
