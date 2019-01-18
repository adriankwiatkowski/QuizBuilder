package com.example.android.quizbuilder.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "quiz")
public class QuizEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private Date date;
    private String name;
    private List<QuizPage> pages;

    @Ignore
    public QuizEntry(QuizEntry quizEntry) {
        this.id = quizEntry.id;
        this.date = quizEntry.date;
        this.name = quizEntry.name;
        List<QuizPage> newList = new ArrayList<>();
        for (int i = 0; i < quizEntry.pages.size(); i++) {
            newList.add(new QuizPage(quizEntry.getPages().get(i)));
        }
        this.pages = newList;
    }

    @Ignore
    public QuizEntry(Date date, String name) {
        this.date = date;
        this.name = name;
        this.pages = new ArrayList<>();
    }

    @Ignore
    public QuizEntry(Date date, String name, List<QuizPage> pages) {
        this.date = date;
        this.name = name;
        this.pages = pages;
    }

    public QuizEntry(long id, Date date, String name, List<QuizPage> pages) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.pages = pages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuizEntry quizEntry = (QuizEntry) o;

        if (getId() != quizEntry.getId()) return false;
        if (!getDate().equals(quizEntry.getDate())) return false;
        if (!getName().equals(quizEntry.getName())) return false;
        return getPages().equals(quizEntry.getPages());
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getDate().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getPages().hashCode();
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "QuizEntry{" +
                "id=" + id +
                ", date=" + date +
                ", name='" + name + '\'' +
                ", pages=" + pages +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<QuizPage> getPages() {
        return pages;
    }

    public void setPages(List<QuizPage> pages) {
        this.pages = pages;
    }
}
