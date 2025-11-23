package cymind.dto.article;

import cymind.dto.comments.CommentDTO;
import cymind.dto.user.ProfessionalDTO;
import cymind.model.Article;
import cymind.model.Comment;
import cymind.model.MentalHealthProfessional;

import java.util.ArrayList;
import java.util.List;

public record ArticleDTO(Long id, String articleName, String category1, String category2, String category3,
                        String content, List<ProfessionalDTO> authors, List<CommentDTO> comments) {
    public ArticleDTO(Article article) {
        this(
            article.getId(),
            article.getArticleName(),
            article.getCategory1(),
            article.getCategory2(),
            article.getCategory3(),
            article.getContent(),
            mapAuthors(article.getAuthors()),
            mapComments(article.getComments())
        );
    }

    private static List<ProfessionalDTO> mapAuthors(List<MentalHealthProfessional> authors) {
        List<ProfessionalDTO> dtoList = new ArrayList<>();

        if (authors == null) {
            return dtoList;
        }

        for (MentalHealthProfessional author : authors) {
            dtoList.add(new ProfessionalDTO(author));
        }

        return dtoList;
    }

    private static List<CommentDTO> mapComments(List<Comment> comments) {
        List<CommentDTO> dtoList = new ArrayList<>();

        if (comments == null) {
            return dtoList;
        }

        for (Comment comment : comments) {
            CommentDTO dto = new CommentDTO(
                    comment.getId(),
                    comment.getContent(),
                    comment.getAuthor().getAbstractUser().getFirstName() + " " + comment.getAuthor().getAbstractUser().getLastName(),
                    comment.getAuthor().getId(),
                    comment.getCreationDate()
            );

            dtoList.add(dto);
        }

        return dtoList;
    }
}
