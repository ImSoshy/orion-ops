package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.tail.FileTailType;
import com.orion.ops.entity.request.FileTailRequest;
import com.orion.ops.entity.vo.FileTailConfigVO;
import com.orion.ops.entity.vo.FileTailVO;
import com.orion.ops.service.api.FileTailService;
import com.orion.ops.utils.Valid;
import com.orion.utils.Charsets;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 文件tail api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/1 23:31
 */
@Api(tags = "日志文件tail")
@RestController
@RestWrapper
@RequestMapping("/orion/api/file-tail")
public class FileTailController {

    @Resource
    private FileTailService fileTailService;

    @PostMapping("/token")
    @ApiOperation(value = "检查并获取日志文件token")
    public FileTailVO getTailToken(@RequestBody FileTailRequest request) {
        FileTailType type = Valid.notNull(FileTailType.of(request.getType()));
        Long relId = Valid.notNull(request.getRelId());
        return fileTailService.getTailToken(type, relId);
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加日志文件")
    @EventLog(EventType.ADD_TAIL_FILE)
    public Long addTailFile(@RequestBody FileTailRequest request) {
        Valid.notBlank(request.getName());
        Valid.notNull(request.getMachineId());
        Valid.notBlank(request.getPath());
        Valid.notNull(request.getOffset());
        Valid.notNull(request.getCharset());
        Valid.isTrue(Files1.isPath(request.getPath()));
        Valid.isTrue(Charsets.isSupported(request.getCharset()));
        return fileTailService.insertTailFile(request);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改日志文件")
    @EventLog(EventType.UPDATE_TAIL_FILE)
    public Integer updateTailFile(@RequestBody FileTailRequest request) {
        Valid.notNull(request.getId());
        if (!Strings.isBlank(request.getPath())) {
            Valid.isTrue(Files1.isPath(request.getPath()));
        }
        if (!Strings.isBlank(request.getCharset())) {
            Valid.isTrue(Charsets.isSupported(request.getCharset()));
        }
        return fileTailService.updateTailFile(request);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除日志文件")
    @EventLog(EventType.DELETE_TAIL_FILE)
    public Integer deleteTailFile(@RequestBody FileTailRequest request) {
        Long id = Valid.notNull(request.getId());
        return fileTailService.deleteTailFile(id);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取日志文件列表")
    public DataGrid<FileTailVO> tailFileList(@RequestBody FileTailRequest request) {
        return fileTailService.tailFileList(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取日志文件详情")
    public FileTailVO tailFileDetail(@RequestBody FileTailRequest request) {
        Long id = Valid.notNull(request.getId());
        return fileTailService.tailFileDetail(id);
    }

    @PostMapping("/config")
    @ApiOperation(value = "获取机器默认配置")
    public FileTailConfigVO getMachineConfig(@RequestBody FileTailRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        return fileTailService.getMachineConfig(machineId);
    }

    @PostMapping("/write")
    @ApiOperation(value = "写入命令")
    public void write(@RequestBody FileTailRequest request) {
        String token = Valid.notBlank(request.getToken());
        String command = Valid.notEmpty(request.getCommand());
        fileTailService.writeCommand(token, command);
    }

}
