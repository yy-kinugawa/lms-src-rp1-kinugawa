package jp.co.sss.lms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import jp.co.sss.lms.dto.CompanyDto;

/**
 * 企業マスタマッパー
 * 
 * @author 絹川 - Task.57
 */
@Mapper
public interface MCompanyMapper {

	List<CompanyDto> getCompanyDto(@Param("deleteFlg") Short deleteFlg);
}
