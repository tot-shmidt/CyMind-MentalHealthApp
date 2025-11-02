package cymind.dto.article;

import cymind.dto.user.ProfessionalDTO;
import cymind.model.Article;
import cymind.model.MentalHealthProfessional;

import java.util.Set;

public record ArticleDTO(Long id,String articleName, String category1, String category2, String category3,
                         String content, Set<MentalHealthProfessional> authors) {
    public ArticleDTO(Article article) {
        this(article.getId(),article.getArticleName(), article.getCategory1(), article.getCategory2(), article.getCategory3(), article.getContent(), article.getAuthors());
    }
}
