# MyBookReader
阅读器

使用开源项目MONKOVEL为基础修改
去掉原有的在线功能，接入 开源项目任阅中提供的api完成在线看书，放入书架

目前问题为
   
    1.项目中的greeddao不知何原因，无法缓存章节数据，导致最近观看的图标上显示当前看到的章节有问题，及每次打开书籍都需要再次拉取一次章节列表接口
     
    2.缓存章节功能还没有接入到项目中。  
       
    3.MONKOVEL功能中的本地书籍功能存在问题
        1).读取较为缓慢
        2).显示格式有误，无法正确的分行
      
    4.无用代码需要整理，并且




===========================分割线====================================

###1.2.5版本更新

#####1.章节数据问题解决正常缓存章节   
    引发新问题 关于缓存，及章节跟新问题
    
#####2.章节离线功能已经完成，能正常下载章节并且离线观看
    但是问题同上

#####3.崩溃捕获模块上线，捕获app中出现的崩溃等错误现象

#####4.页面显示小说行数调整，更好的适应手机尺寸

####TODO
    1.章节更新问题处理。
    2.优化使用ui交互。
    3.优化本地文件读取


版本太老了，后续考虑kotlin重构原生，flutter写一个全平台，目前没有小说的数据来源，后面考虑加入自己下载的读取，作为自用，或者个别人用