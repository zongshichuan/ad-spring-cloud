# ad-spring-cloud

广告系统设计与实现

技术栈：spring cloud alibaba 、kafka、shyiko 

涉及技术：倒排索引设计、binlog监听、kafka发布订阅

核心功能：
1、倒排索引设计实现，保存广告索引数据在JVM
2、Binlog日志监听，把变动数据发布到kafka，再由kafka订阅数据更新到JVM内存
