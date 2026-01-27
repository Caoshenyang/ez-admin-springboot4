package com.ez.admin.api.system;

import com.ez.admin.common.model.model.PageQuery;
import com.ez.admin.common.model.model.PageVO;
import com.ez.admin.common.model.model.R;
import com.ez.admin.dto.dict.req.*;
import com.ez.admin.dto.dict.vo.DictDataListVO;
import com.ez.admin.dto.dict.vo.DictDataDetailVO;
import com.ez.admin.dto.dict.vo.DictTypeDetailVO;
import com.ez.admin.dto.dict.vo.DictTypeListVO;
import com.ez.admin.service.dict.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理控制器
 * <p>
 * 权限说明：本控制器的权限通过路由拦截式鉴权实现，无需使用 @SaCheckPermission 注解
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@RestController
@RequestMapping("/dict")
@RequiredArgsConstructor
@Tag(name = "字典管理", description = "字典类型和字典数据管理")
public class DictController {

    private final DictService dictService;

    // ==================== 字典类型 ====================

    @PostMapping("/type")
    @Operation(summary = "创建字典类型", description = "创建新的字典类型")
    public R<Void> createType(@Valid @RequestBody DictTypeCreateReq request) {
        log.info("创建字典类型请求，字典名称：{}", request.getDictName());
        dictService.createDictType(request);
        return R.success("创建成功");
    }

    @PutMapping("/type")
    @Operation(summary = "更新字典类型", description = "更新字典类型信息")
    public R<Void> updateType(@Valid @RequestBody DictTypeUpdateReq request) {
        log.info("更新字典类型请求，字典ID：{}", request.getDictId());
        dictService.updateDictType(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/type/{dictId}")
    @Operation(summary = "删除字典类型", description = "根据字典ID删除字典类型")
    public R<Void> deleteType(@PathVariable Long dictId) {
        log.info("删除字典类型请求，字典ID：{}", dictId);
        dictService.deleteDictType(dictId);
        return R.success("删除成功");
    }

    @GetMapping("/type/{dictId}")
    @Operation(summary = "查询字典类型详情", description = "根据字典ID查询字典类型完整信息")
    public R<DictTypeDetailVO> getTypeById(@PathVariable Long dictId) {
        DictTypeDetailVO dictType = dictService.getDictTypeById(dictId);
        return R.success(dictType);
    }

    @PostMapping("/type/page")
    @Operation(summary = "分页查询字典类型", description = "分页查询字典类型列表")
    public R<PageVO<DictTypeListVO>> getTypePage(@RequestBody PageQuery query) {
        PageVO<DictTypeListVO> page = dictService.getDictTypePage(query);
        return R.success(page);
    }

    @GetMapping("/type/list")
    @Operation(summary = "查询所有字典类型", description = "查询所有字典类型列表（不分页）")
    public R<List<DictTypeListVO>> getTypeList() {
        List<DictTypeListVO> list = dictService.getDictTypeList();
        return R.success(list);
    }

    // ==================== 字典数据 ====================

    @PostMapping("/data")
    @Operation(summary = "创建字典数据", description = "创建新的字典数据")
    public R<Void> createData(@Valid @RequestBody DictDataCreateReq request) {
        log.info("创建字典数据请求，字典标签：{}", request.getDictLabel());
        dictService.createDictData(request);
        return R.success("创建成功");
    }

    @PutMapping("/data")
    @Operation(summary = "更新字典数据", description = "更新字典数据信息")
    public R<Void> updateData(@Valid @RequestBody DictDataUpdateReq request) {
        log.info("更新字典数据请求，字典数据ID：{}", request.getDictDataId());
        dictService.updateDictData(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/data/{dictDataId}")
    @Operation(summary = "删除字典数据", description = "根据字典数据ID删除字典数据")
    public R<Void> deleteData(@PathVariable Long dictDataId) {
        log.info("删除字典数据请求，字典数据ID：{}", dictDataId);
        dictService.deleteDictData(dictDataId);
        return R.success("删除成功");
    }

    @GetMapping("/data/list/{dictId}")
    @Operation(summary = "根据字典类型查询数据", description = "根据字典类型ID查询字典数据列表")
    public R<List<DictDataListVO>> getDataListByDictId(@PathVariable Long dictId) {
        List<DictDataListVO> list = dictService.getDictDataListByDictId(dictId);
        return R.success(list);
    }

    @GetMapping("/data/type/{dictType}")
    @Operation(summary = "根据字典类型编码查询数据", description = "根据字典类型编码查询字典数据列表")
    public R<List<DictDataListVO>> getDataListByDictType(@PathVariable String dictType) {
        List<DictDataListVO> list = dictService.getDictDataListByDictType(dictType);
        return R.success(list);
    }

    @DeleteMapping("/data/batch")
    @Operation(summary = "批量删除字典数据", description = "批量删除多个字典数据")
    public R<Void> batchDeleteData(@Valid @RequestBody DictDataBatchDeleteReq request) {
        log.info("批量删除字典数据请求，数量：{}", request.getDictDataIds().size());
        dictService.batchDeleteDictData(request.getDictDataIds());
        return R.success("批量删除成功");
    }

    @PostMapping("/data/page")
    @Operation(summary = "分页查询字典数据", description = "分页查询字典数据列表，支持多条件筛选")
    public R<PageVO<DictDataDetailVO>> getDataPage(@RequestBody PageQuery query) {
        PageVO<DictDataDetailVO> page = dictService.getDictDataPage(query);
        return R.success(page);
    }

    @PutMapping("/type/status")
    @Operation(summary = "切换字典类型状态", description = "切换指定字典类型的状态（启用/禁用）")
    public R<Void> changeDictTypeStatus(@Valid @RequestBody DictTypeStatusChangeReq request) {
        log.info("切换字典类型状态请求，字典ID：{}，状态：{}", request.getDictId(), request.getStatus());
        dictService.changeDictTypeStatus(request);
        return R.success("状态切换成功");
    }
}
