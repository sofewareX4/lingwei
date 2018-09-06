package com.sumixer.ys.manager.service.store.impl;

import com.sumixer.ys.manager.config.AppConst;
import com.sumixer.ys.manager.dao.*;
import com.sumixer.ys.manager.entity.*;
import com.sumixer.ys.manager.service.common.dto.ItemDTO;
import com.sumixer.ys.manager.service.common.dto.PageDTO;
import com.sumixer.ys.manager.service.store.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    @Autowired
    private YsStoreMapper ysStoreMapper;
    @Autowired
    private YsStoreCloseTimeMapper ysStoreCloseTimeMapper;
    @Autowired
    private YsStorePathMapper ysStorePathMapper;
    @Autowired
    private YsWxStoreSlideMapper ysWxStoreSlideMapper;
    @Autowired
    private YsStoreMenuMapper ysStoreMenuMapper;
    @Autowired
    private YsStoreNoticeMapper ysStoreNoticeMapper;
    @Autowired
    private YsGoodsCategoryMapper ysGoodsCategoryMapper;
    @Autowired
    private YsGoodsMapper ysGoodsMapper;
    @Autowired
    private YsStoreGoodsMapper ysStoreGoodsMapper;

    @Override
    public void changeStoreStatus(Date nowDate) {
        YsStoreExample example = new YsStoreExample();
        example.createCriteria().andStatusNotEqualTo(AppConst.STORE_FREEZED).andStatusNotEqualTo(AppConst.STORE_DELETE);
        List<YsStore> ysStores = ysStoreMapper.selectByExample(example);
        for(YsStore store : ysStores){

            YsStoreCloseTimeExample ysStoreCloseTimeExample = new YsStoreCloseTimeExample();
            ysStoreCloseTimeExample.createCriteria()
                    .andBeginTimeLessThanOrEqualTo(nowDate)
                    .andEndTimeGreaterThanOrEqualTo(nowDate)
                    .andStoreIdEqualTo(store.getStoreId());
            if(ysStoreCloseTimeMapper.selectByExample(ysStoreCloseTimeExample).size()!=0){
                store.setStatus(AppConst.STORE_HOLIDAY);
                updateStore(store);
            }
            else{
                store.setStatus(AppConst.STORE_NORMAL);
                updateStore(store);
            }

            if(store.getOpenBeginTime().equals(store.getOpenEndTime())){
                store.setStatus(AppConst.STORE_HOLIDAY);
                updateStore(store);
            }


        }
    }

    @Override
    public YsStore getStoreByStoreId(String storeId) {
        YsStoreExample example=new YsStoreExample();
        YsStoreExample.Criteria criteria=example.createCriteria();
        criteria.andStoreIdEqualTo(storeId);
        List<YsStore> stores=ysStoreMapper.selectByExample(example);
        if(!stores.isEmpty()){
            return stores.get(0);
        }
        return null;
    }

    @Override
    public void updateStore(YsStore store) {
        YsStoreExample example = new YsStoreExample();
        example.createCriteria().andStoreIdEqualTo(store.getStoreId());
        ysStoreMapper.updateByExampleSelective(store, example);
    }

    @Override
    public List<YsStorePath> getPathByStoreId(String storeId) {
        YsStorePathExample example = new YsStorePathExample();
        example.createCriteria().andStoreIdEqualTo(storeId);
        List<YsStorePath> ysStorePathList = ysStorePathMapper.selectByExample(example);
        return ysStorePathList;
    }

    @Override
    public YsStorePath addPath(YsStorePath path) {
        ysStorePathMapper.insertSelective(path);
        YsStorePathExample example = new YsStorePathExample();
        example.createCriteria().andPathIdEqualTo(path.getPathId());
        return ysStorePathMapper.selectByExample(example).get(0);

    }

    @Override
    public void delPath(String pathId) {

        YsStorePathExample example = new YsStorePathExample();
        example.createCriteria().andPathIdEqualTo(pathId);
        ysStorePathMapper.deleteByExample(example);
    }

    @Override
    public YsStorePath getPathByPathId(String pathId) {
        YsStorePathExample example = new YsStorePathExample();
        example.createCriteria().andPathIdEqualTo(pathId);
        List<YsStorePath> ysStorePaths = ysStorePathMapper.selectByExample(example);
        if(ysStorePaths.size()!=0){

            return ysStorePaths.get(0);
        }
        return null;
    }

    @Override
    public List<YsStoreCloseTime> getStoreCloseTimes(String storeId) {
        YsStoreCloseTimeExample example = new YsStoreCloseTimeExample();
        YsStoreCloseTimeExample.Criteria criteria = example.createCriteria();
        criteria.andStoreIdEqualTo(storeId);
        return ysStoreCloseTimeMapper.selectByExample(example);
    }

    @Override
    public void updateStoreOpenTimes(YsStore store, List<YsStoreCloseTime> times) {
        YsStoreExample storeExample = new YsStoreExample();
        YsStoreExample.Criteria storeExampleCriteria = storeExample.createCriteria();
        storeExampleCriteria.andStoreIdEqualTo(store.getStoreId());
        ysStoreMapper.updateByExampleSelective(store,storeExample);
        YsStoreCloseTimeExample closeTimeExample = new YsStoreCloseTimeExample();
        closeTimeExample.createCriteria().andStoreIdEqualTo(store.getStoreId());
        ysStoreCloseTimeMapper.deleteByExample(closeTimeExample);
        for (YsStoreCloseTime time : times) {
            ysStoreCloseTimeMapper.insert(time);
        }
    }

    @Override
    public List<YsWxStoreSlide> getWxSlides(String storeId) {
        YsWxStoreSlideExample example = new YsWxStoreSlideExample();
        YsWxStoreSlideExample.Criteria criteria = example.createCriteria();
        criteria.andYsStoreIdEqualTo(storeId);
        List<YsWxStoreSlide> slides = ysWxStoreSlideMapper.selectByExample(example);
        return slides;
    }

    @Override
    public List<YsStoreNotice> getWxNotices(String storeId) {
        YsStoreNoticeExample example = new YsStoreNoticeExample();
        example.createCriteria().andStoreIdEqualTo(storeId);
        List<YsStoreNotice> notices = ysStoreNoticeMapper.selectByExample(example);
        return notices;
    }

    @Override
    public List<YsStoreMenu> getWxMenus(String storeId) {
        YsStoreMenuExample example = new YsStoreMenuExample();
        example.createCriteria().andStoreIdEqualTo(storeId);
        List<YsStoreMenu> menus = ysStoreMenuMapper.selectByExample(example);
        return menus;
    }

    /**
     * 查找goods的分类信息
     * @param goods
     * @param dto
     */
    private void setGoodsCategory(YsGoods goods,ItemDTO dto){
        YsGoodsCategory category = ysGoodsCategoryMapper.selectByCategoryId(goods.getCategoryId());
        if (!AppConst.ROOTCATEGORY.equals(category.getParentId())){
            dto.setSecondCategory(category.getCategoryName());
            YsGoodsCategory pCategory = ysGoodsCategoryMapper.selectByCategoryId(category.getParentId());
            dto.setFirstCategory(pCategory.getCategoryName());
        }else{
            dto.setFirstCategory(category.getCategoryName());
        }
    }

    @Override
    public List<YsGoods> getWxGoods(String idsStr,String storeId) {
        String[] ids = idsStr.split(",");
        List<YsGoods> goodsList = new ArrayList();
        for(String id:ids){
            YsStoreGoodsExample storeGoodsExample = new YsStoreGoodsExample();
            storeGoodsExample.createCriteria().andGoodsIdEqualTo(id).andStoreIdEqualTo(storeId).andPublishEqualTo(AppConst.PUBLISHED);
            List<YsStoreGoods> storeGoodsList = ysStoreGoodsMapper.selectByExample(storeGoodsExample);
            if(storeGoodsList.isEmpty()){
                continue;
            }

            YsGoods goods = ysGoodsMapper.selectByGoodsId(id);
            if (goods == null || goods.getPublish() != AppConst.PUBLISHED){
                continue;
            }
            goodsList.add(goods);
        }
        return goodsList;
    }

    @Override
    public PageDTO<ItemDTO> getWxGoods(String storeId, int pageIndex, int pageSize) {
        PageDTO<ItemDTO> pageDTO = new PageDTO();
        pageDTO.setPageIndex(pageIndex);
        pageDTO.setPageSize(pageSize);
        pageDTO.setData(new ArrayList());
        YsStoreGoodsExample storeGoodsExample = new YsStoreGoodsExample();
        storeGoodsExample.createCriteria().andStoreIdEqualTo(storeId).andPublishEqualTo(AppConst.PUBLISHED);
        List<YsStoreGoods> storeGoodsList = ysStoreGoodsMapper.selectByExample(storeGoodsExample);
        for(YsStoreGoods storeGoods:storeGoodsList){
            YsGoods goods = ysGoodsMapper.selectByGoodsId(storeGoods.getGoodsId());
            if(goods.getPublish() == AppConst.PUBLISHED){
                ItemDTO dto = new ItemDTO();
                dto.setPublish("已上架");
                dto.setYsGoods(goods);
                dto.setYsStoreGoods(storeGoods);
                setGoodsCategory(goods,dto);
                pageDTO.getData().add(dto);
            }
        }
        return pageDTO;
    }

    @Override
    public List<ItemDTO> getGoodsInfoList(String idsStr, String storeId) {
        List<YsGoods> goodsList = getWxGoods(idsStr,storeId);
        List<ItemDTO> itemDTOs = new ArrayList();
        for(YsGoods goods:goodsList){
            ItemDTO dto = new ItemDTO();
            dto.setYsGoods(goods);
            setGoodsCategory(goods,dto);
            itemDTOs.add(dto);
        }
        return itemDTOs;
    }

    private void updateWxSlides(String storeId, List<YsWxStoreSlide> slides){
        YsWxStoreSlideExample example = new YsWxStoreSlideExample();
        example.createCriteria().andYsStoreIdEqualTo(storeId);
        ysWxStoreSlideMapper.deleteByExample(example);
        for (YsWxStoreSlide slide: slides){
            ysWxStoreSlideMapper.insert(slide);
        }
    }

    private void updateWxNotices(String storeId, List<YsStoreNotice> notices){
        YsStoreNoticeExample example = new YsStoreNoticeExample();
        example.createCriteria().andStoreIdEqualTo(storeId);
        ysStoreNoticeMapper.deleteByExample(example);
        for (YsStoreNotice notice: notices){
            ysStoreNoticeMapper.insert(notice);
        }
    }

    @Override
    public void updateWxManage(String storeId, List<YsWxStoreSlide> slides, List<YsStoreNotice> notices) {
        updateWxSlides(storeId, slides);
        updateWxNotices(storeId,notices);
    }

    @Override
    public void delMenu(String menuId) {
        YsStoreMenuExample example = new YsStoreMenuExample();
        example.createCriteria().andMenuIdEqualTo(menuId);
        ysStoreMenuMapper.deleteByExample(example);
    }

    @Override
    public YsStoreMenu addMenu(YsStoreMenu menu) {
        ysStoreMenuMapper.insert(menu);
        YsStoreMenuExample example = new YsStoreMenuExample();
        example.createCriteria().andMenuIdEqualTo(menu.getMenuId());
        List<YsStoreMenu> menus = ysStoreMenuMapper.selectByExample(example);
        if(menus.isEmpty()){
            return null;
        }
        return menus.get(0);
    }

    @Override
    public YsStoreMenu updateMenu(String menuId, String menuName, String goodsId) {
        YsStoreMenuExample example = new YsStoreMenuExample();
        example.createCriteria().andMenuIdEqualTo(menuId);
        List<YsStoreMenu> menus = ysStoreMenuMapper.selectByExample(example);
        if(menus.isEmpty()){
            return null;
        }
        YsStoreMenu menu = menus.get(0);
        YsStoreMenuExample menuExample = new YsStoreMenuExample();
        menuExample.createCriteria().andIdEqualTo(menu.getId());
        menu.setMenuName(menuName);
        menu.setGoodsId(goodsId);
        menu.setUpdateTime(new Date());
        ysStoreMenuMapper.updateByExample(menu,menuExample);
        return menu;
    }
}
