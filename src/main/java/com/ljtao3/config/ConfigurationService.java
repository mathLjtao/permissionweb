package com.ljtao3.config;

import com.ljtao3.beans.PageQuery;
import com.ljtao3.beans.PageResult;
import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.convert.BaseConvert;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ConfigurationService {

    @Resource
    private ConfigurationDao configurationDao;

    public List<Configuration> getAll() {
        return configurationDao.getAll();
    }

    public PageResult<Configuration> getByPage(PageQuery page) {
        BaseConvert.checkPara(page);
        int count = configurationDao.count();
        if (count > 0) {
            List<Configuration> list = configurationDao.getByPage(page);
            return PageResult.<Configuration>builder().total(count).data(list).build();
        }
        return PageResult.<Configuration>builder().build();
    }

    public Configuration saveOrUpdate(ConfigurationParam param) {
        Configuration configuration = configurationDao.findByK(param.getK());
        if (configuration == null) {
            configuration = generate(param);
            configurationDao.insert(configuration);
        } else {
            configuration.setV(param.getV());
            configuration.setOperator(MyRequestHolder.getCurrentUser().getUsername());
            configuration.setComment(param.getComment());
            configurationDao.updateByK(configuration);
        }
        return configuration;
    }

    private Configuration generate(ConfigurationParam param) {
        return new Configuration(param.getK(), param.getV(), MyRequestHolder.getCurrentUser().getUsername(), param.getComment());
    }
}