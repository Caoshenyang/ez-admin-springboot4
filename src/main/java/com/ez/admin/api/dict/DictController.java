package com.ez.admin.api.dict;

import com.ez.admin.common.model.PageQuery;
import com.ez.admin.common.model.PageVO;
import com.ez.admin.common.model.R;
import com.ez.admin.dto.dict.req.*;
import com.ez.admin.dto.dict.vo.DictDataListVO;
import com.ez.admin.dto.dict.vo.DictTypeDetailVO;
import com.ez.admin.dto.dict.vo.DictTypeListVO;
import com.ez.admin.service.dict.DictService;
import com.ez.admin.common.permission.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理控制器
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
    @SaCheckPermission("system:dict:create")
    @Operation(summary = "创建字典类型", description = "创建新的字典类型")
    public R<Void> createType(@Valid @RequestBody DictTypeCreateReq request) {
        log.info("创建字典类型请求，字典名称：{}", request.getDictName());
        dictService.createDictType(request);
        return R.success("创建成功");
    }

    @PutMapping("/type")
    @SaCheckPermission("system:dict:update")
    @Operation(summary = "更新字典类型", description = "更新字典类型信息")
    public R<Void> updateType(@Valid @RequestBody DictTypeUpdateReq request) {
        log.info("更新字典类型请求，字典ID：{}", request.getDictId());
        dictService.updateDictType(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/type/{dictId}")
    @SaCheckPermission("system:dict:delete")
    @Operation(summary = "删除字典类型", description = "根据字典ID删除字典类型")
    public R<Void> deleteType(@PathVariable Long dictId) {
        log.info("删除字典类型请求，字典ID：{}", dictId);
        dictService.deleteDictType(dictId);
        return R.success("删除成功");
    }

    @GetMapping("/type/{dictId}")
    @SaCheckPermission("system:dict:query")
    @Operation(summary = "查询字典类型详情", description = "根据字典ID查询字典类型完整信息")
    public R<DictTypeDetailVO> getTypeById(@PathVariable Long dictId) {
        DictTypeDetailVO dictType = dictService.getDictTypeById(dictId);
        return R.success(dictType);
    }

    @PostMapping("/type/page")
    @SaCheckPermission("system:dict:query")
    @Operation(summary = "分页查询字典类型", description = "分页查询字典类型列表")
    public R<PageVO<DictTypeListVO>> getTypePage(@RequestBody PageQuery query) {
        PageVO<DictTypeListVO> page = dictService.getDictTypePage(query);
        return R.success(page);
    }

    @GetMapping("/type/list")
    @SaCheckPermission("system:dict:query")
    @Operation(summary = "查询所有字典类型", description = "查询所有字典类型列表（不分页）")
    public R<List<DictTypeListVO>> getTypeList() {
        List<DictTypeListVO> list = dictService.getDictTypeList();
        return R.success(list);
    }

    // ==================== 字典数据 ====================

    @PostMapping("/data")
    @SaCheckPermission("system:dict:create")
    @Operation(summary = "创建字典数据", description = "创建新的字典数据")
    public R<Void> createData(@Valid @RequestBody DictDataCreateReq request) {
        log.info("创建字典数据请求，字典标签：{}", request.getDictLabel());
        dictService.createDictData(request);
        return R.success("创建成功");
    }

    @PutMapping("/data")
    @SaCheckPermission("system:dict:update")
    @Operation(summary = "更新字典数据", description = "更新字典数据信息")
    public R<Void> updateData(@Valid @RequestBody DictDataUpdateReq request) {
        log.info("更新字典数据请求，字典数据ID：{}", request.getDictDataId());
        dictService.updateDictData(request);
        return R.success("更新成功");
    }

    @DeleteMapping("/data/{dictDataId}")
    @SaCheckPermission("system:dict:delete")
    @Operation(summary = "删除字典数据", description = "根据字典数据ID删除字典数据")
    public R<Void> deleteData(@PathVariable Long dictDataId) {
        log.info("删除字典数据请求，字典数据ID：{}", dictDataId);
        dictService.deleteDictData(dictDataId);
        return R.success("删除成功");
    }

    @GetMapping("/data/list/{dictId}")
    @SaCheckPermission("system:dict:query")
    @Operation(summary = "根据字典类型查询数据", description = "根据字典类型ID查询字典数据列表")
    public R<List<DictDataListVO>> getDataListByDictId(@PathVariable Long dictId) {
        List<DictDataListVO> list = dictService.getDictDataListByDictId(dictId);
        return R.success(list);
    }

    @GetMapping("/data/type/{dictType}")
    @SaCheckPermission("system:dict:query")
    @Operation(summary = "根据字典类型编码查询数据", description = "根据字典类型编码查询字典数据列表")
    public R<List<DictDataListVO>> getDataListByDictType(@PathVariable String dictType) {
        List<DictDataListVO> list = dictService.getDictDataListByDictType(dictType);
        return R.success(list);
    }
}
