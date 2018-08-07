package com.ceyentra.quiz.data;

public class Question {
    private String question;
    private String[] answers;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public int getAnswerIndex(String answer){
        for (int i=0;i<answers.length;i++){
            if (answer.equals(answers[i])){
                return i;
            }
        }
        return -1;
    }
}
