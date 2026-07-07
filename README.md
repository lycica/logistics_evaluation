# 物流服务商评价系统

## 项目简介

基于 Spring Boot + Thymeleaf 的物流服务商评价与管理平台。用户可对物流服务商进行多维度评分（时效性、服务态度、包裹完好度、价格），发表文字评论并上传图片凭证；系统根据评分生成综合排行和单项排行榜；管理员可管理用户、服务商和评价，并提供数据看板与可视化图表。

## 技术栈

| 层面 | 技术 |
|------|------|
| 后端框架 | Spring Boot 4.0.6 |
| 模板引擎 | Thymeleaf |
| ORM | Spring Data JPA（Hibernate） |
| 数据库 | MySQL 8 |
| 密码加密 | BCrypt（spring-security-crypto） |
| 前端图表 | ECharts |
| 构建工具 | Maven |
| Java 版本 | JDK 17 |

## 功能模块

| 一级功能 | 二级子功能 | 功能详述 |
|----------|------------|----------|
| 用户管理 | 用户注册 | 填写用户名、手机号/邮箱、密码等信息，系统自动分配普通用户角色 |
|  | 用户登录 | 支持账号密码登录 |
|  | 用户退出 | 登录后可退出账号 |
|  | 用户修改 | 登录后用户可修改密码 |
|  | 用户删除 | 登录后用户删除自己的账号 |
|  | 管理员用户管理 | 查看所有用户列表（支持按用户名搜索），强制删除违规用户 |
| 物流服务商信息管理 | 添加服务商 | 管理员录入服务商编号、名称、联系方式等信息 |
|  | 修改服务商信息 | 管理员修改服务商编号、名称、联系方式等信息 |
|  | 删除服务商 | 管理员可删除服务商 |
|  | 查询服务商 | 支持按服务商名称、编号等关键字模糊查询，结果按综合评分排序展示 |
| 评价与评分 | 提交评价 | 选择物流服务商，填写四项评分（时效性、服务态度、包裹完好度、价格，百分制），撰写文字评论（支持敏感词过滤），上传图片凭证（支持多张，限制格式和大小） |
|  | 修改评价 | 用户可以修改自己发表的评价，系统记录修改历史 |
|  | 删除评价 | 用户可删除自己发表的评价 |
|  | 管理员处理评价 | 查看所有评价列表，支持按服务商、评分、时间筛选，对违规评价进行隐藏或删除 |
| 收藏 | 用户收藏物流服务商 | 支持用户对信任的服务商进行收藏操作 |
| 综合排行与展示 | 综合实力排行榜 | 根据四项评分的加权平均计算综合得分，按得分从高到低展示服务商列表 |
|  | 单项维度排行榜 | 提供速度最快、服务最好、最完好、性价比最高四个独立排行榜，用户可切换查看 |
| 数据统计可视化 | 总体概览看板 | 展示系统核心指标：总评价数、参与用户数、服务商总数、整体平均综合得分 |
|  | 服务商评分对比图 | 以柱状图形式展示所有服务商的综合得分及各维度得分，便于管理员横向对比 |

## 项目结构

```
src/main/java/com/logistics/logistics_evaluation/
├── LogisticsEvaluationApplication.java    -- 应用入口
├── config/
│   ├── SecurityConfig.java                -- BCrypt 密码编码器
│   ├── WebConfig.java                     -- 登录拦截器注册
│   └── WebMvcConfig.java                  -- 静态资源映射（图片上传）
├── interceptor/
│   └── LoginInterceptor.java              -- 登录认证 & 管理员权限拦截
├── entity/
│   ├── User.java                          -- 用户实体
│   ├── ServiceProvider.java               -- 服务商实体
│   ├── Review.java                        -- 评价实体
│   ├── Favorite.java                      -- 收藏实体
│   └── Image.java                         -- 评价图片实体
├── repository/
│   ├── UserRepository.java
│   ├── ServiceProviderRepository.java
│   ├── ReviewRepository.java
│   ├── FavoriteRepository.java
│   └── ImageRepository.java
├── service/
│   ├── UserService.java
│   ├── ServiceProviderService.java
│   ├── ReviewService.java
│   └── FavoriteService.java
├── service/impl/
│   ├── UserServiceImpl.java
│   ├── ServiceProviderServiceImpl.java
│   ├── ReviewServiceImpl.java
│   └── FavoriteServiceImpl.java
└── controller/
    ├── UserController.java                -- 用户注册/登录/退出/收藏
    ├── ProviderController.java            -- 服务商浏览与收藏操作
    ├── ReviewController.java              -- 评价提交/修改/删除
    ├── RankingController.java             -- 排行榜展示
    └── AdminController.java               -- 管理员后台
src/main/resources/
├── application.yml                        -- 应用配置
├── templates/                             -- Thymeleaf 视图模板
│   ├── index.html                         -- 首页
│   ├── rankings.html                      -- 排行榜页
│   ├── user/
│   │   ├── login.html                     -- 登录页
│   │   └── register.html                  -- 注册页
│   ├── provider/
│   │   ├── list.html                      -- 服务商列表
│   │   └── detail.html                    -- 服务商详情（含评价）
│   ├── review/
│   │   ├── review-form.html               -- 评价表单
│   │   └── my-reviews.html                -- 我的评价列表
│   ├── favorite/
│   │   └── list.html                      -- 我的收藏列表
│   └── admin/
│       ├── dashboard.html                 -- 管理员仪表盘
│       ├── users.html                     -- 用户管理
│       ├── provider-list.html             -- 服务商管理列表
│       ├── provider-form.html             -- 服务商新增/编辑表单
│       ├── reviews.html                   -- 评价管理
│       └── statistics.html                -- 数据统计看板
└── static/js/
    └── echarts.min.js                     -- ECharts 图表库
```

## 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE logistics_eval DEFAULT CHARACTER SET utf8mb4;
```

按实体类定义手动建表，或临时将 `application.yml` 中 `ddl-auto` 改为 `update` 以自动建表。

### 2. 配置数据库连接

编辑 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/logistics_eval?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8
    username: root
    password: 123456
```

### 3. 启动应用

```bash
# 编译并运行
mvn spring-boot:run

# 或打包运行
mvn clean package -DskipTests
java -jar target/logistics_evaluation-0.0.1-SNAPSHOT.jar
```

### 4. 访问系统

- 普通用户首页：http://localhost:8080/
- 登录/注册：http://localhost:8080/user/login

## 数据库表

| 表名 | 说明 |
|------|------|
| user | 用户表（username, password, email, role） |
| service_provider | 服务商表（name, contact, avg_score, review_count） |
| review | 评价表（user_id, provider_id, timeliness_score, attitude_score, integrity_score, price_score, comment_text） |
| favorite | 收藏表（user_id, provider_id, favorite_time） |
| image | 评价图片表（review_id, image_path） |

## 注意事项

- 系统采用 Session 认证，Interceptor 拦截除登录/注册/静态资源外的所有请求
- 管理员角色需直接在数据库中设置 `role = 'admin'`
- 图片上传至 `./upload/images/` 目录，通过 `/upload/**` 路径访问
- Hibernate `ddl-auto` 默认为 `validate`，首次部署需手动建表
- 敏感词过滤内置简单词表，可按需扩展