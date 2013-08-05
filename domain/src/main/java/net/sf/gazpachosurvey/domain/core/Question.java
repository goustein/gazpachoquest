package net.sf.gazpachosurvey.domain.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import net.sf.gazpachosurvey.domain.i18.QuestionTranslation;
import net.sf.gazpachosurvey.types.Language;
import net.sf.gazpachosurvey.types.QuestionType;

import net.sf.gazpachosurvey.domain.support.AbstractPersistable;

@Entity
public class Question extends AbstractPersistable<Integer> {

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    @ManyToOne
    private Question parent;

    @ManyToOne
    private Page page;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderColumn(name = "order_in_subquestions")
    private List<Question> subquestions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", orphanRemoval = true)
    @OrderColumn(name = "order_in_question")
    private List<Answer> answers;

    private String title;

    boolean isRequired;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    private Language language;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "language")
    private Map<Language, QuestionTranslation> translations;

    public Question() {
        super();
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public List<Question> getSubquestions() {
        return subquestions;
    }

    public void setSubquestions(List<Question> subquestions) {
        this.subquestions = subquestions;
    }

    public Question getParent() {
        return parent;
    }

    public void setParent(Question parent) {
        this.parent = parent;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Map<Language, QuestionTranslation> getTranslations() {
        if (translations == null) {
            translations = new HashMap<>();
        }
        return translations;
    }

    public void setTranslations(Map<Language, QuestionTranslation> translations) {
        this.translations = translations;
    }

    public void setTranslation(Language language, String text) {
        QuestionTranslation translation = new QuestionTranslation();
        translation.setText(text);
        translation.setQuestion(this);
        translation.setLanguage(language);
        getTranslations().put(language, translation);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

}