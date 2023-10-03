package com.clonecoding.steam.service.community;

import com.clonecoding.steam.dto.common.PaginationListDto;
import com.clonecoding.steam.dto.community.BoardDTO;
import com.clonecoding.steam.enums.community.BoardType;
import org.springframework.data.domain.Pageable;

public interface BoardService {

    /*
     * @author: parkjunha
     * @description: 게시판 조회
     * @param: section 게시판 타입
     * @param: page 페이지네이션
     * @return: 주어진 게시판 타입과 페이지 정보에 따라 조회된 게시글 목록을 포함하는 PaginationListDto 객체.
     * 만약 조회된 게시글이 없다면, 빈 PaginationListDto 객체를 반환
     */
    PaginationListDto<BoardDTO.Preview> getBoardList(BoardType section , Pageable page);

    /*
     * @author: parkjunha
     * @description: 게시판 삭제
     * @param: section 게시판 타입
     * @param: boardId 게시판 id
     * @param: userId 유저 id
     * @return:
     */
    void deleteBoard(BoardType section, String boardId, String userId);

    /*
     * @author: parkjunha
     * @description:
     * @param: section 게시판 타입
     * @param: boardId 게시판 id
     * @return: 게시글 상세보기 BoardDTO.Detail 객체.
     */
    BoardDTO.Detail getBoardDetail(BoardType section, String boardId);

    /*
     * @methodName: createBoard
     * @author: parkjunha
     * @description: 게시글 생성 요청
     * @param: section 게시판 타입
     * @param: req 게시글 생성시 필요한 정보
     * @return:
     */
    void createBoard(BoardType section, BoardDTO.Create req);

    /*
     * @author: parkjunha
     * @description: 게시글 상세 보기 내에서 댓글 조회
     * @param: section 게시판 타입
     * @param: boardId 게시판 id
     * @return: 지정된 게시판 글에 연결된 댓글 목록을 포함하는 PaginationListDto 객체를 반환
     * 댓글이 없는 경우 빈 PaginationListDto 객체를 반환
     */
    PaginationListDto<BoardDTO.Comment> getComment(BoardType section, String boardId);



}
