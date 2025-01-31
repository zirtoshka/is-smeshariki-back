package itma.smesharikiback.services;

import com.github.demidko.aot.WordformMeaning;
import itma.smesharikiback.models.*;
import itma.smesharikiback.models.reposirories.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;

@Service
@AllArgsConstructor
public class PsychoService {

    private final BlockingQueue<Post> postQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Comment> commentQueue = new LinkedBlockingQueue<>();
    private final TriggerWordRepository triggerWordRepository;
    private final PostTriggerWordRepository postTriggerWordRepository;
    private final ApplicationForTreatmentRepository applicationForTreatmentRepository;
    private final ApplicationForTreatmentPropensityRepository applicationForTreatmentPropensityRepository;
    private final CommentTriggerWordRepository commentTriggerWordRepository;

    public void addToPostQueue(Post task) {
        try {
            postQueue.put(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void addToCommentQueue(Comment task) {
        try {
            commentQueue.put(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Async
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void processPostQueue() {
        try {

            Post task = postQueue.take();
            System.out.println("Обработка поста: " + task);

            Pair<ArrayList<String>, HashMap<String, TriggerWord>> pair = getTriggerWordsWithTextWords(task.getText());
            ArrayList<String> text = pair.getLeft();
            HashMap<String, TriggerWord> words = pair.getRight();

            ArrayList<PostTriggerWord> postTriggerWords = new ArrayList<>();
            for (String word : text) {
                List<WordformMeaning> meanings = lookupForMeanings(word);
                if (!meanings.isEmpty()) {
                    String lemma = String.valueOf(meanings.getFirst().getLemma());
                    if (words.containsKey(lemma)) {
                        PostTriggerWord postTriggerWord = new PostTriggerWord();
                        postTriggerWord.setPost(task);
                        postTriggerWord.setTriggerWord(words.get(lemma));
                        postTriggerWords.add(postTriggerWordRepository.save(postTriggerWord));
                    }
                }
            }

            if (!postTriggerWords.isEmpty()) {
                ApplicationForTreatment applicationForTreatment = new ApplicationForTreatment();
                applicationForTreatment.setPost(task);
                applicationForTreatment.setStatus(GeneralStatus.NEW);
                applicationForTreatmentRepository.save(applicationForTreatment);

                for (PostTriggerWord postTriggerWord : postTriggerWords) {
                    ApplicationForTreatmentPropensity applicationForTreatmentPropensity =
                            new ApplicationForTreatmentPropensity();
                    applicationForTreatmentPropensity.setPropensity(postTriggerWord.getTriggerWord().getPropensity());
                    applicationForTreatmentPropensity.setApplicationForTreatment(applicationForTreatment);
                    applicationForTreatmentPropensityRepository.save(applicationForTreatmentPropensity);
                }
                System.out.println("Создана заявка на лечение по посту: " + task);

            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Async
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void processCommentQueue() {
        try {

            Comment task = commentQueue.take();
            System.out.println("Обработка коммента: " + task);

            Pair<ArrayList<String>, HashMap<String, TriggerWord>> pair = getTriggerWordsWithTextWords(task.getText());
            ArrayList<String> text = pair.getLeft();
            HashMap<String, TriggerWord> words = pair.getRight();

            ArrayList<CommentTriggerWord> commentTriggerWords = new ArrayList<>();
            for (String word : text) {
                List<WordformMeaning> meanings = lookupForMeanings(word);
                if (!meanings.isEmpty()) {
                    String lemma = String.valueOf(meanings.getFirst().getLemma());
                    if (words.containsKey(lemma)) {
                        CommentTriggerWord commentTriggerWord = new CommentTriggerWord();
                        commentTriggerWord.setComment(task);
                        commentTriggerWord.setTriggerWord(words.get(lemma));
                        commentTriggerWords.add(commentTriggerWordRepository.save(commentTriggerWord));
                    }
                }
            }

            if (!commentTriggerWords.isEmpty()) {
                ApplicationForTreatment applicationForTreatment = new ApplicationForTreatment();
                applicationForTreatment.setComment(task);
                applicationForTreatment.setStatus(GeneralStatus.NEW);
                applicationForTreatmentRepository.save(applicationForTreatment);

                for (CommentTriggerWord commentTriggerWord : commentTriggerWords) {
                    ApplicationForTreatmentPropensity applicationForTreatmentPropensity =
                            new ApplicationForTreatmentPropensity();
                    applicationForTreatmentPropensity.setPropensity(commentTriggerWord.getTriggerWord().getPropensity());
                    applicationForTreatmentPropensity.setApplicationForTreatment(applicationForTreatment);
                    applicationForTreatmentPropensityRepository.save(applicationForTreatmentPropensity);
                }
                System.out.println("Создана заявка на лечение по комменту: " + task);

            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Pair<ArrayList<String>, HashMap<String, TriggerWord>> getTriggerWordsWithTextWords(String rawText) {
        List<TriggerWord> triggerWords = triggerWordRepository.findAll();
        HashMap<String, TriggerWord> triggerWordHashMap = new HashMap<>();
        for (TriggerWord triggerWord : triggerWords) {
            List<WordformMeaning> meanings = lookupForMeanings(triggerWord.getWord().toLowerCase());
            if (!meanings.isEmpty()) {
                triggerWordHashMap.put(String.valueOf(meanings.getFirst().getLemma()), triggerWord);
            }
        }

        String[] text = rawText.split("\\s");
        ArrayList<String> postTriggerWords = new ArrayList<>();
        for (String word : text) {
            word = word.replaceAll("[^A-Za-zА-Яа-я0-9]", "").toLowerCase();
            List<WordformMeaning> meanings = lookupForMeanings(word);
            if (!meanings.isEmpty()) {
                String lemma = String.valueOf(meanings.getFirst().getLemma());
                postTriggerWords.add(lemma);
            }
        }

        return Pair.of(postTriggerWords, triggerWordHashMap);
    }
}

