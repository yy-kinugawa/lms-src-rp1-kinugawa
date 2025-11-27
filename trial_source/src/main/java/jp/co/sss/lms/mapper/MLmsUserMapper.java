package jp.co.sss.lms.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.sss.lms.dto.UserDetailDto;
import jp.co.sss.lms.form.AttendanceListForm;

/**
 * LMSユーザーマスタマッパー
 * 
 * @author 東京ITスクール
 */
@Mapper
public interface MLmsUserMapper {

	/**
	 * ユーザー基本情報取得
	 * 
	 * @param lmsUserId
	 * @param deleteFlg
	 * @return ユーザー基本情報DTO
	 */
	UserDetailDto getUserDetail(@Param("lmsUserId") Integer lmsUserId,
			@Param("deleteFlg") Short deleteFlg);

	/**
	 * ユーザー基本情報(検索用)取得
	 * 
	 * 
	 * @author 絹川 - Task.57
	 * @param role
	 * @param closeTime
	 * @param deleteFlg
	 * @param attendanceLoginForm
	 * @return ユーザー基本情報DTOリスト
	 */
	List<UserDetailDto> getUserDetailForSearch(@Param("role") String role, @Param("closeTime") Date closeTime, 
			@Param("deleteFlg") Short deleteFlg, AttendanceListForm attendanceListForm);
}
