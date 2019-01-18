package com.example.android.quizbuilder.data.database;

import java.util.ArrayList;
import java.util.List;

public class QuizPage {

    private int type;
    private String question;
    private List<String> correctAnswers;
    private List<String> answers;

    public QuizPage(QuizPage quizPage) {
        this.type = quizPage.type;
        this.question = quizPage.question;
        this.correctAnswers = new ArrayList<>(quizPage.getCorrectAnswers());
        this.answers = new ArrayList<>(quizPage.getAnswers());
    }

    public QuizPage(int type, String question, List<String> correctAnswers, List<String> answers) {
        this.type = type;
        this.question = question;
        this.correctAnswers = correctAnswers;
        this.answers = answers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuizPage quizPage = (QuizPage) o;

        if (getType() != quizPage.getType()) return false;
        if (!getQuestion().equals(quizPage.getQuestion())) return false;
        if (!getCorrectAnswers().equals(quizPage.getCorrectAnswers())) return false;
        return getAnswers().equals(quizPage.getAnswers());
    }

    @Override
    public int hashCode() {
        int result = getType();
        result = 31 * result + getQuestion().hashCode();
        result = 31 * result + getCorrectAnswers().hashCode();
        result = 31 * result + getAnswers().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "QuizPage{" +
                "type=" + type +
                ", question='" + question + '\'' +
                ", correctAnswers=" + correctAnswers +
                ", answers=" + answers +
                '}';
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(List<String> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
}
