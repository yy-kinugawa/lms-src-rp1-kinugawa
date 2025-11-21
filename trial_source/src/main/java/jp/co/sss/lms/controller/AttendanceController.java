package jp.co.sss.lms.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.lms.dto.AttendanceManagementDto;
import jp.co.sss.lms.dto.LoginUserDto;
import jp.co.sss.lms.form.AttendanceForm;
import jp.co.sss.lms.form.DailyAttendanceForm;
import jp.co.sss.lms.service.StudentAttendanceService;
import jp.co.sss.lms.util.AttendanceUtil;
import jp.co.sss.lms.util.Constants;

/**
 * 勤怠管理コントローラ
 * 
 * @author 東京ITスクール
 */
@Controller
@RequestMapping("/attendance")
public class AttendanceController {

	@Autowired
	private StudentAttendanceService studentAttendanceService;
	@Autowired
	private LoginUserDto loginUserDto;
	// 絹川 - Task.27
	@Autowired
	private AttendanceUtil attendanceUtil;

	/**
	 * 勤怠管理画面 初期表示
	 * 
	 * @param lmsUserId
	 * @param courseId
	 * @param model
	 * @return 勤怠管理画面
	 * @throws ParseException
	 */
	@RequestMapping(path = "/detail", method = RequestMethod.GET)
	public String index(Model model) {

		// 勤怠一覧の取得
		List<AttendanceManagementDto> attendanceManagementDtoList = studentAttendanceService
				.getAttendanceManagement(loginUserDto.getCourseId(), loginUserDto.getLmsUserId());
		model.addAttribute("attendanceManagementDtoList", attendanceManagementDtoList);

		// 絹川 - Task.25
		model.addAttribute("notEnterFlg", studentAttendanceService.notEnterCount(loginUserDto.getLmsUserId()));
		
		return "attendance/detail";
	}

	/**
	 * 勤怠管理画面 『出勤』ボタン押下
	 * 
	 * @param model
	 * @return 勤怠管理画面
	 */
	@RequestMapping(path = "/detail", params = "punchIn", method = RequestMethod.POST)
	public String punchIn(Model model) {

		// 更新前のチェック
		String error = studentAttendanceService.punchCheck(Constants.CODE_VAL_ATWORK);
		model.addAttribute("error", error);
		// 勤怠登録
		if (error == null) {
			String message = studentAttendanceService.setPunchIn();
			model.addAttribute("message", message);
		}
		// 一覧の再取得
		List<AttendanceManagementDto> attendanceManagementDtoList = studentAttendanceService
				.getAttendanceManagement(loginUserDto.getCourseId(), loginUserDto.getLmsUserId());
		model.addAttribute("attendanceManagementDtoList", attendanceManagementDtoList);

		return "attendance/detail";
	}

	/**
	 * 勤怠管理画面 『退勤』ボタン押下
	 * 
	 * @param model
	 * @return 勤怠管理画面
	 */
	@RequestMapping(path = "/detail", params = "punchOut", method = RequestMethod.POST)
	public String punchOut(Model model) {

		// 更新前のチェック
		String error = studentAttendanceService.punchCheck(Constants.CODE_VAL_LEAVING);
		model.addAttribute("error", error);
		// 勤怠登録
		if (error == null) {
			String message = studentAttendanceService.setPunchOut();
			model.addAttribute("message", message);
		}
		// 一覧の再取得
		List<AttendanceManagementDto> attendanceManagementDtoList = studentAttendanceService
				.getAttendanceManagement(loginUserDto.getCourseId(), loginUserDto.getLmsUserId());
		model.addAttribute("attendanceManagementDtoList", attendanceManagementDtoList);

		return "attendance/detail";
	}

	/**
	 * 勤怠管理画面 『勤怠情報を直接編集する』リンク押下
	 * 
	 * @param model
	 * @return 勤怠情報直接変更画面
	 */
	@RequestMapping(path = "/update")
	public String update(Model model) {

		// 勤怠管理リストの取得
		List<AttendanceManagementDto> attendanceManagementDtoList = studentAttendanceService
				.getAttendanceManagement(loginUserDto.getCourseId(), loginUserDto.getLmsUserId());
		// 勤怠フォームの生成
		AttendanceForm attendanceForm = studentAttendanceService
				.setAttendanceForm(attendanceManagementDtoList);
		model.addAttribute("attendanceForm", attendanceForm);

		return "attendance/update";
	}

	/**
	 * 勤怠情報直接変更画面 『更新』ボタン押下
	 * 
	 * @param attendanceForm
	 * @param model
	 * @param result
	 * @return 勤怠管理画面
	 * @throws ParseException
	 */
	@RequestMapping(path = "/update", params = "complete", method = RequestMethod.POST)
	public String complete(AttendanceForm attendanceForm, BindingResult result, Model model)
			throws ParseException {
		
		//入力エラーがあれば勤怠管理直接変更画面へ遷移
		// 絹川 - Task.27
		setUpdateErrors(attendanceForm, result);
		if(result.hasErrors()) {
			attendanceForm.setBlankTimes(attendanceUtil.setBlankTime());
			attendanceForm.setMinuteTimes(attendanceUtil.setMinuteTimes());
			attendanceForm.setHourTimes(attendanceUtil.setHourTimes());
			model.addAttribute("attendanceForm", attendanceForm);
			
			return "attendance/update";
		}
		
		// 更新
		String message = studentAttendanceService.update(attendanceForm);
		model.addAttribute("message", message);
		// 一覧の再取得
		List<AttendanceManagementDto> attendanceManagementDtoList = studentAttendanceService
				.getAttendanceManagement(loginUserDto.getCourseId(), loginUserDto.getLmsUserId());
		model.addAttribute("attendanceManagementDtoList", attendanceManagementDtoList);

		return "attendance/detail";
	}

	/**
	 * 勤怠更新時バリデーション
	 * 
	 * @author 絹川 - Task.27
	 * @param attendanceForm
	 * @param result
	 */
	private void setUpdateErrors(AttendanceForm attendanceForm, BindingResult result) {
		
		for(int i = 0; i < attendanceForm.getAttendanceList().size(); i++) {
			DailyAttendanceForm form = attendanceForm.getAttendanceList().get(i);
			
			//備考欄エラー
			if(!(form.getNote().isEmpty()) && form.getNote().length()>100) {
				result.rejectValue("attendanceList[" + i + "].note", Constants.VALID_KEY_MAXLENGTH, 
						new Object[] {"備考", 100}, "不正な入力です。");
			}
			
			//出勤時間入力エラー
			if((!(form.getTrainingStartTimeHourValue().isEmpty()) && form.getTrainingStartTimeMinuteValue().isEmpty())) {
				result.rejectValue("attendanceList[" + i + "].trainingStartTimeMinuteValue", Constants.INPUT_INVALID, 
						new Object[] {"出勤時間"}, "不正な入力です。");
			}else if((form.getTrainingStartTimeHourValue().isEmpty() && !(form.getTrainingStartTimeMinuteValue().isEmpty()))) {
				result.rejectValue("attendanceList[" + i + "].trainingStartTimeHourValue", Constants.INPUT_INVALID, 
						new Object[] {"出勤時間"}, "不正な入力です。");
			}
			
			//退勤時間入力エラー
			if((!(form.getTrainingEndTimeHourValue().isEmpty()) && form.getTrainingEndTimeMinuteValue().isEmpty())) {
				result.rejectValue("attendanceList[" + i + "].trainingEndTimeMinuteValue", Constants.INPUT_INVALID, 
						new Object[] {"退勤時間"}, "不正な入力です。");
			}else if((form.getTrainingEndTimeHourValue().isEmpty() && !(form.getTrainingEndTimeMinuteValue().isEmpty()))) {
				result.rejectValue("attendanceList[" + i + "].trainingEndTimeHourValue", Constants.INPUT_INVALID, 
						new Object[] {"退勤時間"}, "不正な入力です。");
			}
			
			//出勤時間未入力・退勤時間入力済みエラー
			if(form.getTrainingStartTimeHourValue().isEmpty() && form.getTrainingStartTimeMinuteValue().isEmpty()
					&& !(form.getTrainingEndTimeHourValue().isEmpty()) && !(form.getTrainingEndTimeMinuteValue().isEmpty())) {
				result.rejectValue("attendanceList[" + i + "].trainingStartTimeHourValue", Constants.VALID_KEY_ATTENDANCE_PUNCHINEMPTY, 
						"不正な入力です。");
				result.rejectValue("attendanceList[" + i + "].trainingStartTimeMinuteValue", "start_error", "");
			}
			
			if(!(form.getTrainingStartTimeHourValue().isEmpty()) && !(form.getTrainingStartTimeMinuteValue().isEmpty())
					&& !(form.getTrainingEndTimeHourValue().isEmpty()) && !(form.getTrainingEndTimeMinuteValue().isEmpty())) {
					int startHour = Integer.parseInt(form.getTrainingStartTimeHourValue());
					int startMinute = Integer.parseInt(form.getTrainingStartTimeMinuteValue());
					int endHour = Integer.parseInt(form.getTrainingEndTimeHourValue());
					int endMinute = Integer.parseInt(form.getTrainingEndTimeMinuteValue());
				
				//出勤時間＜退勤時間エラー
				if((endHour*60 + endMinute) <= (startHour*60 + startMinute)) {
					result.rejectValue("attendanceList[" + i + "].trainingStartTimeHourValue", Constants.VALID_KEY_ATTENDANCE_TRAININGTIMERANGE, 
							new Object[] {i}, "不正な入力です。");
					result.rejectValue("attendanceList[" + i + "].trainingStartTimeMinuteValue", "start_error", "");
				}
				
				//勤務時間＜中抜け時間エラー
				if(!(form.getBlankTime() == null) && ((endHour*60 + endMinute) - (startHour*60 + startMinute)) < form.getBlankTime()) {
					result.rejectValue("attendanceList[" + i + "].blankTime", Constants.VALID_KEY_ATTENDANCE_BLANKTIMEERROR, 
							"不正な入力です。");
				}
			}	
		}
	}
	
	
}