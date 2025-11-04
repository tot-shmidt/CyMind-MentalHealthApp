package cymind.service;

import cymind.dto.article.ArticleDTO;
import cymind.dto.article.CreateArticleDTO;
import cymind.enums.ExerciseType;
import cymind.enums.UserType;
import cymind.model.AbstractUser;
import cymind.model.Article;
import cymind.model.Exercise;
import cymind.model.MentalHealthProfessional;
import cymind.repository.AbstractUserRepository;
import cymind.repository.ArticleRepository;
import cymind.repository.ExerciseRepository;
import cymind.repository.MentalHealthProfessionalRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ArticleService {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    AbstractUserRepository abstractUserRepository;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    MentalHealthProfessionalRepository mentalHealthProfessionalRepository;

    @Transactional
    public ArticleDTO getArticleById(long id) throws NoResultException, AuthorizationDeniedException {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new NoResultException("Article not found");
        }
        // Check if endpoint is not called from outside the application
        checkAuth();

        return new ArticleDTO(article);
    }

    @Transactional
    public ArticleDTO createNewArticle(CreateArticleDTO createArticleDTO) throws AuthorizationDeniedException, NoResultException {
        MentalHealthProfessional professional = mentalHealthProfessionalRepository.findByAbstractUserId(createArticleDTO.userId());

        if  (professional == null) {
            throw new NoResultException("Professional not found");
        }
        checkAuth(professional);

        // Create new Article object from createArticleDTO
        Article article = new Article(
                createArticleDTO.articleName(), createArticleDTO.category1(), createArticleDTO.category2(),
                createArticleDTO.category3(), createArticleDTO.content(), professional
        );

        articleRepository.save(article);
        return new ArticleDTO(article);
    }

    @Transactional
    public ArticleDTO updateArticle(long id, CreateArticleDTO createArticleDTO) throws AuthorizationDeniedException, NoResultException {
        MentalHealthProfessional professional = mentalHealthProfessionalRepository.findByAbstractUserId(createArticleDTO.userId());
        if (professional == null) {
            throw new NoResultException("Professional not found");
        }

        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new NoResultException("Article not found");
        }

        checkAuth(professional);

        //Update all fields of the article
        article.setArticleName(createArticleDTO.articleName());
        article.setCategory1(createArticleDTO.category1());
        article.setCategory2(createArticleDTO.category2());
        article.setCategory3(createArticleDTO.category3());
        article.setContent(createArticleDTO.content());

        // Check if it is a new professional editing, and we need to add a new author
        List<MentalHealthProfessional> authors = article.getAuthors();
        if (!authors.contains(professional)) {
            authors.add(professional);
            article.setAuthors(authors);
        }

        articleRepository.save(article);
        return new ArticleDTO(article);
    }

    @Transactional
    public List<ArticleDTO> getNumberOfArticles(int number) {
        checkAuth();

        if (number <= 0) {
            throw new IllegalArgumentException("Number of articles requested must be positive");
        }

        List<Article> allArticles = articleRepository.findAll();
        List<ArticleDTO> result = new ArrayList<>();

        // Get requested number of articles
        int count = 0;
        for (Article article : allArticles) {
            if (count >= number) {
                break;
            }
            result.add(new ArticleDTO(article));
            count++;
        }

        return result;
    }

    @Transactional
    public void deleteArticle(long id, long userId) throws NoResultException, AuthorizationDeniedException {
        Article article = articleRepository.findById(id);
        if (article == null) {
            throw new NoResultException("Article not found");
        }

        MentalHealthProfessional professional = mentalHealthProfessionalRepository.findByAbstractUserId(userId);
        if (professional == null) {
            throw new NoResultException("Professional not found");
        }

        checkAuth(professional);

        articleRepository.deleteById(id);
    }

    @Transactional
    public List<Exercise> getExercisesForArticle(long articleId) throws NoResultException, AuthorizationDeniedException {
        Article article = articleRepository.findById(articleId);
        if (article == null) {
            throw new NoResultException("Article not found");
        }

        checkAuth();

        // List will keep all related exercises
        List<Exercise> result = new ArrayList<>();

        addExercisesForCategory(article.getCategory1(), result);
        addExercisesForCategory(article.getCategory2(), result);
        addExercisesForCategory(article.getCategory3(), result);

        return result;
    }

    /**
     * Helper method for getExercisesForArticle which selects one exercise from a category
     */
    private void addExercisesForCategory(String category, List<Exercise> result) {
        if (category == null) {
            return;
        }

        ExerciseType type = switch (category.toUpperCase()) {
            case "STRESS" -> ExerciseType.MEDITATION;
            case "SLEEP" -> ExerciseType.SLEEP_HYGIENE;
            case "FOCUS" -> ExerciseType.MINDFUL_WALKING;
            case "SELF_ESTEEM" -> ExerciseType.GRATITUDE_PRACTICE;
            case "ANXIETY" -> ExerciseType.BREATHING;
            case "POSITIVITY" -> ExerciseType.AFFIRMATION;
            case "AWARENESS" -> ExerciseType.JOURNALING;
            default -> null;
        };

        if (type != null) {
            List<Exercise> allExercises = exerciseRepository.findByExerciseType(type);
            if (!allExercises.isEmpty()) {
                //Pick random one from particular type.
                Random random = new Random();
                Exercise randomExercise = allExercises.get(random.nextInt(allExercises.size()));
                result.add(randomExercise);
            }
        }
    }

    /**
     * Frontend can get pages of a particular number(size) of articles.
     * @param page
     * @param size
     * @return
     */
    @Transactional
    public List<ArticleDTO> getPaginatedArticles(int page, int size) {
        checkAuth();

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page must be >= 0 and size must be > 0");
        }

        List<Article> allArticles = articleRepository.findAll();
        List<ArticleDTO> result = new ArrayList<>();

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allArticles.size());

        if (startIndex < allArticles.size()) {
            for (int i = startIndex; i < endIndex; i++) {
                result.add(new ArticleDTO(allArticles.get(i)));
            }
        }

        return result;
    }

    /**
     * This lets frontend fetch articles by specific category.
     * @param category
     * @return
     */
    @Transactional
    public List<ArticleDTO> getArticlesByCategory(String category) {
        checkAuth();

        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }

        List<Article> allArticles = articleRepository.findAll();
        List<ArticleDTO> result = new ArrayList<>();

        for (Article article : allArticles) {
            if (category.equalsIgnoreCase(article.getCategory1()) ||
                category.equalsIgnoreCase(article.getCategory2()) ||
                category.equalsIgnoreCase(article.getCategory3())) {
                result.add(new ArticleDTO(article));
            }
        }

        return result;
    }

    private void checkAuth() throws AuthorizationDeniedException {
        AbstractUser abstractUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (abstractUser == null) {
            throw new AuthorizationDeniedException("Authentication Required. Not Guest, Student or Mental Health Professional");
        }
    }

    private void checkAuth(MentalHealthProfessional professional) throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getUserType() != UserType.PROFESSIONAL) {
            throw new AuthorizationDeniedException("Attempting to access a mood entry for different user");
        }
    }

}
