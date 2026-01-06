package com.ezadmin.api;

import com.ezadmin.common.response.Result;
import com.ezadmin.common.response.page.PageQuery;
import com.ezadmin.common.response.page.PageVO;
import com.ezadmin.model.dto.DictDataCreateDTO;
import com.ezadmin.model.dto.DictDataUpdateDTO;
import com.ezadmin.model.dto.DictTypeCreateDTO;
import com.ezadmin.model.dto.DictTypeUpdateDTO;
import com.ezadmin.model.query.DictDataQuery;
import com.ezadmin.model.query.DictTypeQuery;
import com.ezadmin.modules.system.entity.DictData;
import com.ezadmin.modules.system.entity.DictType;
import com.ezadmin.service.DictManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理接口
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/system/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictManagementService dictManagementService;

    // ---------- 字典类型 ----------
    @Operation(summary = "字典类型分页查询")
    @PostMapping("/type/page")
    public Result<PageVO<DictType>> pageType(@RequestBody PageQuery<DictTypeQuery> query) {
        return Result.success(dictManagementService.pageType(query));
    }

    @Operation(summary = "字典类型详情")
    @GetMapping("/type/{dictId}")
    public Result<DictType> typeDetail(@PathVariable Long dictId) {
        return Result.success(dictManagementService.typeDetail(dictId));
    }

    @Operation(summary = "新增字典类型")
    @PostMapping("/type/create")
    public Result<Void> createType(@RequestBody DictTypeCreateDTO dto) {
        dictManagementService.createType(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "更新字典类型")
    @PostMapping("/type/update")
    public Result<Void> updateType(@RequestBody DictTypeUpdateDTO dto) {
        dictManagementService.updateType(dto);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/type/{dictId}")
    public Result<Void> deleteType(@PathVariable Long dictId) {
        dictManagementService.deleteType(dictId);
        return Result.success("删除成功");
    }

    @Operation(summary = "全部字典类型列表")
    @GetMapping("/type/list")
    public Result<List<DictType>> listAllTypes() {
        return Result.success(dictManagementService.listAllTypes());
    }

    // ---------- 字典数据 ----------
    @Operation(summary = "字典数据分页查询")
    @PostMapping("/data/page")
    public Result<PageVO<DictData>> pageData(@RequestBody PageQuery<DictDataQuery> query) {
        return Result.success(dictManagementService.pageData(query));
    }

    @Operation(summary = "字典数据详情")
    @GetMapping("/data/{dictDataId}")
    public Result<DictData> dataDetail(@PathVariable Long dictDataId) {
        return Result.success(dictManagementService.dataDetail(dictDataId));
    }

    @Operation(summary = "新增字典数据")
    @PostMapping("/data/create")
    public Result<Void> createData(@RequestBody DictDataCreateDTO dto) {
        dictManagementService.createData(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "更新字典数据")
    @PostMapping("/data/update")
    public Result<Void> updateData(@RequestBody DictDataUpdateDTO dto) {
        dictManagementService.updateData(dto);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除字典数据")
    @DeleteMapping("/data/{dictDataId}")
    public Result<Void> deleteData(@PathVariable Long dictDataId) {
        dictManagementService.deleteData(dictDataId);
        return Result.success("删除成功");
    }

    @Operation(summary = "根据字典类型获取数据列表")
    @GetMapping("/data/list/{dictId}")
    public Result<List<DictData>> listDataByType(@PathVariable Long dictId) {
        return Result.success(dictManagementService.listDataByType(dictId));
    }
}
