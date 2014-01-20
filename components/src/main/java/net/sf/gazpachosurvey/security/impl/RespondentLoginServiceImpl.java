package net.sf.gazpachosurvey.security.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.sf.gazpachosurvey.domain.core.Participant;
import net.sf.gazpachosurvey.domain.core.PersonalInvitation;
import net.sf.gazpachosurvey.domain.core.Questionnair;
import net.sf.gazpachosurvey.domain.core.Study;
import net.sf.gazpachosurvey.domain.support.Invitation;
import net.sf.gazpachosurvey.domain.support.Person;
import net.sf.gazpachosurvey.repository.InvitationRepository;
import net.sf.gazpachosurvey.repository.QuestionnairRepository;
import net.sf.gazpachosurvey.repository.qbe.SearchParameters;
import net.sf.gazpachosurvey.security.LoginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("respondentLoginService")
public class RespondentLoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(RespondentLoginServiceImpl.class);

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private QuestionnairRepository questionnairRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Person login(final String userName, final String password) {
        Invitation invitation = invitationRepository.findOneByExample(Invitation.with().token(password).build(),
                new SearchParameters().caseSensitive().equals());
        if (invitation == null) {
            return null; // TODO Throw authentication exception
        }

        Participant participant = null;
        List<Questionnair> questionnairs = new ArrayList<>();
        Study study = invitation.getStudy();
        if (invitation instanceof PersonalInvitation) {
            PersonalInvitation personalInvitation = (PersonalInvitation) invitation;
            participant = personalInvitation.getParticipant();
            Questionnair example = Questionnair.with().participant(participant).study(study).build();
            questionnairs = questionnairRepository.findByExample(example, new SearchParameters());
        }

        if (questionnairs.isEmpty()) {
            participant = Participant.with().firstname("anonymous").build();
            Questionnair questionnair = Questionnair.with().study(study).build();
            questionnair = questionnairRepository.save(questionnair);
            questionnairs.add(questionnair);
        }
        entityManager.detach(participant);
        for (Questionnair questionnair : questionnairs) {
            entityManager.detach(questionnair);
            participant.getQuestionnairs().add(questionnair);
        }
        logger.info("Access granted to Respondent with name {} ", participant.getFirstname());
        return participant;
    }
}
