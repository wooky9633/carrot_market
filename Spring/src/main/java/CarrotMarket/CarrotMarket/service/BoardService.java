package CarrotMarket.CarrotMarket.service;

import CarrotMarket.CarrotMarket.domain.Board;
import CarrotMarket.CarrotMarket.repository.BoardRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public Board postBoard(Board board) {
        //TODO : XSS 대비 문자열 관리
        //board.setText(board.getText().replaceAll("[<>/]", ""));
        System.out.println(board.getPrice());
        System.out.println(board.getCategoryId());
        System.out.println(board.getTitle());
        System.out.println(board.getText());
        System.out.println(board.getRegisterDate());
        System.out.println(board.getDeadlineDate());
        System.out.println(board.getChatCnt());
        System.out.println(board.getDibsCnt());
        System.out.println(board.getViewCnt());
        System.out.println(board.getPicture());
        return boardRepository.save(board);
    }

    public List<Board> searchBoard(String text) {
        //TODO
        return null;
    }

    @Transactional
    public Board editBoard(Board board) {
        Board preBoard = boardRepository.findById(board.getId()).get();
        preBoard.setTitle(board.getTitle());
        preBoard.setText(board.getText());
        preBoard.setPrice(board.getPrice());
        boardRepository.save(preBoard);
        return preBoard;
    }

    public List<Board> findMyBoard(String nickname) {
        return boardRepository.loadByNickname(nickname);
    }

    public Optional<Board> findPostById(Long id) {
        return boardRepository.findById(id);
    }
}
