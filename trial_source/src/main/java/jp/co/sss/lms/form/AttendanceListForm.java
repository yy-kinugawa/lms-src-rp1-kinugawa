package jp.co.sss.lms.form;

import java.util.List;

import lombok.Data;

/**
 * 勤怠情報確認フォーム
 * 
 * @author 絹川 - Task.57
 */
@Data
public class AttendanceListForm {

	/** コース名 */
	private String courseName;
	/** 企業名 */
	private String companyName;
	/** ユーザー名 */
	private String userName;
	/** 会場IDリスト */
	private List<Integer> placeIdList;
	
	/** コース名リスト(表示用) */
	private List<String> courseNameList;
	/** 企業名リスト(表示用) */
	private List<String> companyNameList;
	/** 会場名(表示用) */
	private String placeName;

}
