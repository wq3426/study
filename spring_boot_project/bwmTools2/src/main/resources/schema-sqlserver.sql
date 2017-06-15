USE [master]
GO
/****** Object:  Database [BMW-Goods-Allocation-Tools]    Script Date: 05/12/2017 14:23:43 ******/
CREATE DATABASE [BMW-Goods-Allocation-Tools] ON PRIMARY
  (NAME = N'BMW-Goods-Allocation-Tools', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL10_50.MSSQLSERVER\MSSQL\DATA\BMW-Goods-Allocation-Tools.mdf', SIZE = 7168 KB, MAXSIZE = UNLIMITED, FILEGROWTH = 1024 KB)
LOG ON
  (NAME = N'BMW-Goods-Allocation-Tools_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL10_50.MSSQLSERVER\MSSQL\DATA\BMW-Goods-Allocation-Tools_log.ldf', SIZE = 22144 KB, MAXSIZE = 2048 GB, FILEGROWTH = 10%)
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET COMPATIBILITY_LEVEL = 100
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
  BEGIN
    EXEC [BMW-Goods-Allocation-Tools].[dbo].[sp_fulltext_database] @action = 'enable'
  END
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET ANSI_NULL_DEFAULT OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET ANSI_NULLS OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET ANSI_PADDING OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET ANSI_WARNINGS OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET ARITHABORT OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET AUTO_CLOSE OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET AUTO_CREATE_STATISTICS ON
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET AUTO_SHRINK OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET AUTO_UPDATE_STATISTICS ON
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET CURSOR_CLOSE_ON_COMMIT OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET CURSOR_DEFAULT GLOBAL
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET CONCAT_NULL_YIELDS_NULL OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET NUMERIC_ROUNDABORT OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET QUOTED_IDENTIFIER OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET RECURSIVE_TRIGGERS OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET DISABLE_BROKER
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET AUTO_UPDATE_STATISTICS_ASYNC OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET DATE_CORRELATION_OPTIMIZATION OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET TRUSTWORTHY OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET ALLOW_SNAPSHOT_ISOLATION OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET PARAMETERIZATION SIMPLE
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET READ_COMMITTED_SNAPSHOT OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET HONOR_BROKER_PRIORITY OFF
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET READ_WRITE
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET RECOVERY FULL
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET MULTI_USER
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET PAGE_VERIFY CHECKSUM
GO
ALTER DATABASE [BMW-Goods-Allocation-Tools] SET DB_CHAINING OFF
GO
EXEC sys.sp_db_vardecimal_storage_format N'BMW-Goods-Allocation-Tools', N'ON'
GO
USE [BMW-Goods-Allocation-Tools]
GO
/****** Object:  Table [dbo].[AuthorityInfo]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AuthorityInfo] (
  [id]               [INT] IDENTITY (1, 1) NOT NULL,
  [name]             [VARCHAR](20)         NULL,
  [code]             [VARCHAR](50)         NULL,
  [parentId]         [INT]                 NULL,
  [sort]             [INT]                 NULL,
  [createBy]         [VARCHAR](50)         NULL,
  [createDate]       [DATETIME]            NULL,
  [lastModifiedBy]   [VARCHAR](50)         NULL,
  [lastModifiedDate] [DATETIME]            NULL,
  CONSTRAINT [PK_AuthorityInfo] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'AuthorityInfo',
                                @level2type = N'COLUMN', @level2name = N'id'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'名称', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'AuthorityInfo',
                                @level2type = N'COLUMN', @level2name = N'name'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'代码', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'AuthorityInfo',
                                @level2type = N'COLUMN', @level2name = N'code'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'父主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'AuthorityInfo',
                                @level2type = N'COLUMN', @level2name = N'parentId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'排序', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'AuthorityInfo',
                                @level2type = N'COLUMN', @level2name = N'sort'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'AuthorityInfo',
                                @level2type = N'COLUMN', @level2name = N'createBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'AuthorityInfo',
                                @level2type = N'COLUMN', @level2name = N'createDate'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'AuthorityInfo',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'AuthorityInfo',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedDate'
GO
/****** Object:  Table [dbo].[RoleInfo]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RoleInfo] (
  [id]               [INT] IDENTITY (1, 1) NOT NULL,
  [name]             [VARCHAR](50)         NULL,
  [createBy]         [VARCHAR](50)         NULL,
  [createDate]       [DATETIME]            NULL,
  [lastModifiedBy]   [VARCHAR](50)         NULL,
  [lastModifiedDate] [DATETIME]            NULL,
  CONSTRAINT [PK_RoleInfo] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'RoleInfo',
                                @level2type = N'COLUMN', @level2name = N'id'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'名称', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'RoleInfo',
                                @level2type = N'COLUMN', @level2name = N'name'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'RoleInfo',
                                @level2type = N'COLUMN', @level2name = N'createBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'RoleInfo',
                                @level2type = N'COLUMN', @level2name = N'createDate'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'RoleInfo',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'RoleInfo',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedDate'
GO
/****** Object:  Table [dbo].[UserInfo]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[UserInfo] (
  [id]               [INT] IDENTITY (1, 1) NOT NULL,
  [username]         [VARCHAR](50)         NULL,
  [password]         [VARCHAR](255)        NULL,
  [createBy]         [VARCHAR](50)         NULL,
  [createDate]       [DATETIME]            NULL,
  [lastModifiedBy]   [VARCHAR](50)         NULL,
  [lastModifiedDate] [DATETIME]            NULL,
  CONSTRAINT [PK_UserInfo] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'UserInfo',
                                @level2type = N'COLUMN', @level2name = N'id'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'账号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'UserInfo',
                                @level2type = N'COLUMN', @level2name = N'username'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'密码', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'UserInfo',
                                @level2type = N'COLUMN', @level2name = N'password'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'UserInfo',
                                @level2type = N'COLUMN', @level2name = N'createBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'UserInfo',
                                @level2type = N'COLUMN', @level2name = N'createDate'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'UserInfo',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'UserInfo',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedDate'
GO
/****** Object:  Table [dbo].[WareHouse]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[WareHouse] (
  [id]                    [INT] IDENTITY (1, 1) NOT NULL,
  [code]                  [VARCHAR](32)         NOT NULL,
  [fullCodeLength]        [INT]                 NULL,
  [stallType]             [VARCHAR](32)         NULL,
  [personDrivingSpeed]    [FLOAT]               NULL,
  [forkliftDrivingSpeed]  [FLOAT]               NULL,
  [pickTruckDrivingSpeed] [FLOAT]               NULL,
  [personPickUpSpeed]     [FLOAT]               NULL,
  [forkliftPickUpSpeed]   [FLOAT]               NULL,
  [pickTuckPickUpSpeed]   [FLOAT]               NULL,
  [createBy]              [VARCHAR](50)         NULL,
  [createDate]            [DATETIME]            NULL,
  [lastModifiedBy]        [VARCHAR](50)         NULL,
  [lastModifiedDate]      [DATETIME]            NULL,
  CONSTRAINT [PK_WareHouse] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'id'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'编号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'code'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'编号总长度 来自于仓库配置表中所有编号长度之和', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'fullCodeLength'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'地摊货位类型', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'stallType'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'人行驶速度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'personDrivingSpeed'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'叉车行驶速度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'forkliftDrivingSpeed'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'捡货车行驶速度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'pickTruckDrivingSpeed'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'人抬胳膊的速度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'personPickUpSpeed'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'叉车抬举的速度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'forkliftPickUpSpeed'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'捡货车抬举的速度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'pickTuckPickUpSpeed'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'createBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'createDate'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouse',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedDate'
GO
/****** Object:  Table [dbo].[WareHouseType]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[WareHouseType] (
  [id]   [INT]         NOT NULL,
  [rank] [INT]         NULL,
  [type] [VARCHAR](50) NULL,
  CONSTRAINT [PK_WareHouseType] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseType',
                                @level2type = N'COLUMN', @level2name = N'id'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'级别', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseType',
                                @level2type = N'COLUMN', @level2name = N'rank'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'类型', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseType',
                                @level2type = N'COLUMN', @level2name = N'type'
GO
/****** Object:  Table [dbo].[WareHouseConfig]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[WareHouseConfig] (
  [id]                  [INT] IDENTITY (1, 1) NOT NULL,
  [wareHouseId]         [INT]                 NULL,
  [wareHouseTypeId]     [INT]                 NULL,
  [wareHouseTypeAlias]  [VARCHAR](50)         NULL,
  [length]              [INT]                 NULL,
  [width]               [INT]                 NULL,
  [primaryPriority]     [INT]                 NULL,
  [standbyPriority]     [INT]                 NULL,
  [mode]                [INT]                 NULL,
  [pickTool]            [INT]                 NULL,
  [handsUpDrivingSpeed] [FLOAT]               NULL,
  [rank]                [INT]                 NULL,
  [createBy]            [VARCHAR](50)         NULL,
  [createDate]          [DATETIME]            NULL,
  [lastModifiedBy]      [VARCHAR](50)         NULL,
  [lastModifiedDate]    [DATETIME]            NULL,
  CONSTRAINT [PK_WareHouseConfig] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseConfig',
                                @level2type = N'COLUMN', @level2name = N'id'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'仓库主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseConfig',
                                @level2type = N'COLUMN', @level2name = N'wareHouseId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'类型主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseConfig',
                                @level2type = N'COLUMN', @level2name = N'wareHouseTypeId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'类型别名', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseConfig',
                                @level2type = N'COLUMN', @level2name = N'wareHouseTypeAlias'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'字符长度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseConfig',
                                @level2type = N'COLUMN', @level2name = N'length'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'宽度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseConfig',
                                @level2type = N'COLUMN', @level2name = N'width'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主优先级 升序、降序、升序奇偶相同、降序奇偶相同和自定义（需要手动设置）五种',
                                @level0type = N'SCHEMA', @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'WareHouseConfig', @level2type = N'COLUMN',
                                @level2name = N'primaryPriority'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'次优先级 升序、降序、升序奇偶相同、降序奇偶相同和自定义（需要手动设置）五种',
                                @level0type = N'SCHEMA', @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'WareHouseConfig', @level2type = N'COLUMN',
                                @level2name = N'standbyPriority'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'模式包括两种 直进直出和U型', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseConfig',
                                @level2type = N'COLUMN', @level2name = N'mode'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'捡货工具 层需要选择拣货工具 1 人 2 叉车 3 捡货车',
                                @level0type = N'SCHEMA', @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'WareHouseConfig', @level2type = N'COLUMN', @level2name = N'pickTool'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'人抬胳膊的速度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseConfig',
                                @level2type = N'COLUMN', @level2name = N'handsUpDrivingSpeed'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'排行', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'WareHouseConfig',
                                @level2type = N'COLUMN', @level2name = N'rank'
GO
/****** Object:  Table [dbo].[UserInfo_RoleInfo]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[UserInfo_RoleInfo] (
  [userId] [INT] NULL,
  [roleId] [INT] NULL
) ON [PRIMARY]
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'用户主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'UserInfo_RoleInfo',
                                @level2type = N'COLUMN', @level2name = N'userId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'角色主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'UserInfo_RoleInfo',
                                @level2type = N'COLUMN', @level2name = N'roleId'
GO
/****** Object:  Table [dbo].[RoleInfo_WareHouse]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RoleInfo_WareHouse] (
  [roleId]      [INT] NOT NULL,
  [wareHouseId] [INT] NOT NULL
) ON [PRIMARY]
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'角色主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'RoleInfo_WareHouse',
                                @level2type = N'COLUMN', @level2name = N'roleId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'仓库主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'RoleInfo_WareHouse',
                                @level2type = N'COLUMN', @level2name = N'wareHouseId'
GO
/****** Object:  Table [dbo].[RoleInfo_AuthorityInfo]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RoleInfo_AuthorityInfo] (
  [roleId]      [INT] NOT NULL,
  [authorityId] [INT] NOT NULL
) ON [PRIMARY]
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'角色主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'RoleInfo_AuthorityInfo',
                                @level2type = N'COLUMN', @level2name = N'roleId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'权限主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'RoleInfo_AuthorityInfo',
                                @level2type = N'COLUMN', @level2name = N'authorityId'
GO
/****** Object:  Table [dbo].[CargoLocationType]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[CargoLocationType] (
  [id]                [INT] IDENTITY (1, 1) NOT NULL,
  [code]              [VARCHAR](50)         NULL,
  [length]            [FLOAT]               NULL,
  [width]             [FLOAT]               NULL,
  [height]            [FLOAT]               NULL,
  [extend]            [BIT]                 NULL,
  [increaseLength]    [FLOAT]               NULL,
  [increaseWidth]     [FLOAT]               NULL,
  [frontIncrease]     [FLOAT]               NULL,
  [backIncrease]      [FLOAT]               NULL,
  [leftIncrease]      [FLOAT]               NULL,
  [rightIncrease]     [FLOAT]               NULL,
  [pallet]            [BIT]                 NULL,
  [total]             [INT]                 NULL,
  [useCount]          [INT]                 NULL,
  [wareHouseId]       [INT]                 NULL,
  [createBy]          [VARCHAR](50)         NULL,
  [createDate]        [DATETIME]            NULL,
  [lastModifiedBy]    [VARCHAR](50)         NULL,
  [lastModifiedDate]  [DATETIME]            NULL,
  [batchNum]          [VARCHAR](50)         NULL,
  [batchSerialNumber] [INT]                 NULL,
  CONSTRAINT [PK_CargoLocationType] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'id'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'编号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'code'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'长度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'length'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'宽度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'width'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'高度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'height'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'是否扩展 1 是 0 否', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'extend'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'扩展长度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'increaseLength'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'扩展宽度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'increaseWidth'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'前边扩展长度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'frontIncrease'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'后边扩展长度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'backIncrease'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'左边扩展长度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'leftIncrease'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'右边扩展长度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'rightIncrease'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'是否为托盘类型 1是 0否', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'pallet'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位总数量', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'total'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位已用量', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'useCount'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'仓库主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'wareHouseId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'createBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'createDate'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedDate'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'批次号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocationType',
                                @level2type = N'COLUMN', @level2name = N'batchNum'
GO
/****** Object:  Table [dbo].[CargoLocation_Data]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[CargoLocation_Data] (
  [id]              [VARCHAR](36) NOT NULL,
  [code]            [VARCHAR](50) NULL,
  [cacheCode]       [VARCHAR](50) NULL,
  [primaryPriority] [INT]         NULL,
  [standbyPriority] [INT]         NULL,
  [pickTool]        [INT]         NULL,
  [drivingSpeed]    [FLOAT]       NULL,
  [pickUpSpeed]     [FLOAT]       NULL,
  [parentId]        [VARCHAR](36) NULL,
  [wareHouseId]     [INT]         NULL,
  [configId]        [INT]         NULL,
  [configType]      [INT]         NULL,
  [distance]        [FLOAT]       NULL,
  [batchNumber]     [VARCHAR](50) NULL,
  CONSTRAINT [PK__CargoLoc__3213E83F25869641] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'id'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'编码', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'code'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'缓存key', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'cacheCode'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主优先级', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'primaryPriority'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'次优先级', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'standbyPriority'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'捡货工具 1 人 2 叉车 3 捡货车', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'pickTool'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'行驶速度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'drivingSpeed'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'捡货速度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'pickUpSpeed'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'父主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'parentId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'仓库主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'wareHouseId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'仓库配置主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'configId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'配置类型 1:区域 2:通道 3:排货架 4:层 5:货位 6:进深',
                                @level0type = N'SCHEMA', @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'CargoLocation_Data', @level2type = N'COLUMN',
                                @level2name = N'configType'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'距离', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'distance'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'批次号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation_Data',
                                @level2type = N'COLUMN', @level2name = N'batchNumber'
GO
/****** Object:  Table [dbo].[CargoLocation]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[CargoLocation] (
  [id]                [INT] IDENTITY (1, 1) NOT NULL,
  [code]              [VARCHAR](50)         NULL,
  [wareHouseId]       [INT]                 NULL,
  [typeId]            [INT]                 NULL,
  [score]             [FLOAT]               NULL,
  [createBy]          [VARCHAR](50)         NULL,
  [createDate]        [DATETIME]            NULL,
  [lastModifiedBy]    [VARCHAR](50)         NULL,
  [lastModifiedDate]  [DATETIME]            NULL,
  [batchNum]          [VARCHAR](50)         NULL,
  [batchSerialNumber] [INT]                 NULL,
  CONSTRAINT [PK_CargoLocation] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation',
                                @level2type = N'COLUMN', @level2name = N'id'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'编号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation',
                                @level2type = N'COLUMN', @level2name = N'code'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'仓库主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation',
                                @level2type = N'COLUMN', @level2name = N'wareHouseId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位类型主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation',
                                @level2type = N'COLUMN', @level2name = N'typeId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'分值', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation',
                                @level2type = N'COLUMN', @level2name = N'score'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation',
                                @level2type = N'COLUMN', @level2name = N'createBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation',
                                @level2type = N'COLUMN', @level2name = N'createDate'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedDate'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'批次号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'CargoLocation',
                                @level2type = N'COLUMN', @level2name = N'batchNum'
GO
/****** Object:  Table [dbo].[Material]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Material] (
  [id]                               [INT] IDENTITY (1, 1) NOT NULL,
  [code]                             [VARCHAR](50)         NULL,
  [maxStore]                         [INT]                 NULL,
  [length]                           [FLOAT]               NULL,
  [width]                            [FLOAT]               NULL,
  [height]                           [FLOAT]               NULL,
  [faceUp]                           [BIT]                 NULL,
  [wareHouseId]                      [INT]                 NULL,
  [cargoLocationId]                  [INT]                 NULL,
  [cargoLocationCode]                [VARCHAR](50)         NULL,
  [cargoLocationScore]               [INT]                 NULL,
  [recommendedLocationId]            [INT]                 NULL,
  [recommendedLocationCode]          [VARCHAR](50)         NULL,
  [recommendedLocationScore]         [INT]                 NULL,
  [allAppropriateLibraryTypes]       [VARCHAR](500)        NULL,
  [extendAllAppropriateLibraryTypes] [VARCHAR](50)         NULL,
  [optimalLocationType]              [VARCHAR](50)         NULL,
  [optimalPlacement]                 [VARCHAR](50)         NULL,
  [recommendedLocationType]          [VARCHAR](50)         NULL,
  [recommendedPlacement]             [VARCHAR](50)         NULL,
  [batchSerialNumber]                [INT]                 NULL,
  [batchNum]                         [VARCHAR](20)         NULL,
  [cargoLocationTypeId]              [INT]                 NULL,
  [optimizationRecommend]            [VARCHAR](50)         NULL,
  [pickUpRate]                       [INT]                 NULL,
  [createBy]                         [VARCHAR](50)         NULL,
  [createDate]                       [DATETIME]            NULL,
  [lastModifiedBy]                   [VARCHAR](50)         NULL,
  [lastModifiedDate]                 [DATETIME]            NULL,
  CONSTRAINT [PK_Material] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'id'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'编号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'code'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'最大存放数量', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'maxStore'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'长度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'length'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'宽度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'width'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'高度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'height'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'正面朝上 1 是 0否', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'faceUp'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'仓库主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'wareHouseId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'cargoLocationId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位编号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'cargoLocationCode'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位分值', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'cargoLocationScore'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'推荐货位主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'recommendedLocationId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'推荐货位编码', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'recommendedLocationCode'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'推荐货位分值', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'recommendedLocationScore'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'所有合适货位类型编号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'allAppropriateLibraryTypes'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'合适货位类型(扩展)', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'extendAllAppropriateLibraryTypes'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'最优货位类型编号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'optimalLocationType'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'最优摆放方式', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'optimalPlacement'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'推荐货位类型编号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'recommendedLocationType'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'推荐摆放方式', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'recommendedPlacement'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位类型主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'cargoLocationTypeId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'选值包括：fast mover、slow mover、新物料(物料没有放到货位上)、空值。',
                                @level0type = N'SCHEMA', @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material', @level2type = N'COLUMN',
                                @level2name = N'optimizationRecommend'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'捡货频率', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'pickUpRate'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'createBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'创建时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'createDate'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改人', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedBy'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'修改时间', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE', @level1name = N'Material',
                                @level2type = N'COLUMN', @level2name = N'lastModifiedDate'
GO
/****** Object:  Table [dbo].[Material_CargoLocationType_Data]    Script Date: 05/12/2017 14:23:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Material_CargoLocationType_Data] (
  [id]                           [INT] IDENTITY (1, 1) NOT NULL,
  [wareHouseId]                  [INT]                 NULL,
  [wareHouseCode]                [VARCHAR](50)         NULL,
  [materialId]                   [INT]                 NULL,
  [materialCode]                 [VARCHAR](50)         NULL,
  [materialLength]               [FLOAT]               NULL,
  [materialWidth]                [FLOAT]               NULL,
  [materialHeight]               [FLOAT]               NULL,
  [materialMaxStore]             [INT]                 NULL,
  [cargoLocationTypeId]          [INT]                 NULL,
  [cargoLocationTypeCode]        [VARCHAR](50)         NULL,
  [cargoLocationTypeCanUseCount] [INT]                 NULL,
  [cargoLocationTypeLength]      [FLOAT]               NULL,
  [cargoLocationTypeWidth]       [FLOAT]               NULL,
  [cargoLocationTypeHeight]      [FLOAT]               NULL,
  [cargoLocationTypeExtend]      [BIT]                 NULL,
  [cargoLocationTypePallet]      [BIT]                 NULL,
  [storeCount]                   [INT]                 NULL,
  [usageRate]                    [FLOAT]               NULL,
  [cargoLocationTypeVolume]      [FLOAT]               NULL,
  [placement]                    [VARCHAR](50)         NULL,
  CONSTRAINT [PK_Material_CargoLocationType_Data] PRIMARY KEY CLUSTERED
    (
      [id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'id'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'仓库主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'wareHouseId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'仓库编号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'wareHouseCode'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'物料主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'materialId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'物料编号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'materialCode'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'物料宽度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'materialLength'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'物料宽度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'materialWidth'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'物料高度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'materialHeight'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'物料最大数量', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'materialMaxStore'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位类型主键', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'cargoLocationTypeId'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位类型编号', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'cargoLocationTypeCode'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位类型可使用数量', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'cargoLocationTypeCanUseCount'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位类型长度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'cargoLocationTypeLength'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位类型宽度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'cargoLocationTypeWidth'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'货位类型高度', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'cargoLocationTypeHeight'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'是否扩展', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'cargoLocationTypeExtend'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'是否为托盘类型', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'cargoLocationTypePallet'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'可存放数量', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'storeCount'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'使用率', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'usageRate'
GO
EXEC sys.sp_addextendedproperty @name = N'MS_Description', @value = N'总存放数量', @level0type = N'SCHEMA',
                                @level0name = N'dbo', @level1type = N'TABLE',
                                @level1name = N'Material_CargoLocationType_Data', @level2type = N'COLUMN',
                                @level2name = N'cargoLocationTypeVolume'
GO
/****** Object:  ForeignKey [FK_WareHouseConfig_WareHouse]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[WareHouseConfig]
  WITH CHECK ADD CONSTRAINT [FK_WareHouseConfig_WareHouse] FOREIGN KEY ([wareHouseId])
REFERENCES [dbo].[WareHouse] ([id])
GO
ALTER TABLE [dbo].[WareHouseConfig]
  CHECK CONSTRAINT [FK_WareHouseConfig_WareHouse]
GO
/****** Object:  ForeignKey [FK_WareHouseConfig_WareHouseType]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[WareHouseConfig]
  WITH CHECK ADD CONSTRAINT [FK_WareHouseConfig_WareHouseType] FOREIGN KEY ([wareHouseTypeId])
REFERENCES [dbo].[WareHouseType] ([id])
GO
ALTER TABLE [dbo].[WareHouseConfig]
  CHECK CONSTRAINT [FK_WareHouseConfig_WareHouseType]
GO
/****** Object:  ForeignKey [FK_UserInfo_RoleInfo_RoleInfo]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[UserInfo_RoleInfo]
  WITH CHECK ADD CONSTRAINT [FK_UserInfo_RoleInfo_RoleInfo] FOREIGN KEY ([roleId])
REFERENCES [dbo].[RoleInfo] ([id])
GO
ALTER TABLE [dbo].[UserInfo_RoleInfo]
  CHECK CONSTRAINT [FK_UserInfo_RoleInfo_RoleInfo]
GO
/****** Object:  ForeignKey [FK_UserInfo_RoleInfo_UserInfo]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[UserInfo_RoleInfo]
  WITH CHECK ADD CONSTRAINT [FK_UserInfo_RoleInfo_UserInfo] FOREIGN KEY ([userId])
REFERENCES [dbo].[UserInfo] ([id])
GO
ALTER TABLE [dbo].[UserInfo_RoleInfo]
  CHECK CONSTRAINT [FK_UserInfo_RoleInfo_UserInfo]
GO
/****** Object:  ForeignKey [FK_RoleInfo_WareHouse_RoleInfo]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[RoleInfo_WareHouse]
  WITH CHECK ADD CONSTRAINT [FK_RoleInfo_WareHouse_RoleInfo] FOREIGN KEY ([roleId])
REFERENCES [dbo].[RoleInfo] ([id])
GO
ALTER TABLE [dbo].[RoleInfo_WareHouse]
  CHECK CONSTRAINT [FK_RoleInfo_WareHouse_RoleInfo]
GO
/****** Object:  ForeignKey [FK_RoleInfo_WareHouse_WareHouse]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[RoleInfo_WareHouse]
  WITH CHECK ADD CONSTRAINT [FK_RoleInfo_WareHouse_WareHouse] FOREIGN KEY ([wareHouseId])
REFERENCES [dbo].[WareHouse] ([id])
GO
ALTER TABLE [dbo].[RoleInfo_WareHouse]
  CHECK CONSTRAINT [FK_RoleInfo_WareHouse_WareHouse]
GO
/****** Object:  ForeignKey [FK_RoleInfo_AuthorityInfo_AuthorityInfo]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[RoleInfo_AuthorityInfo]
  WITH CHECK ADD CONSTRAINT [FK_RoleInfo_AuthorityInfo_AuthorityInfo] FOREIGN KEY ([authorityId])
REFERENCES [dbo].[AuthorityInfo] ([id])
GO
ALTER TABLE [dbo].[RoleInfo_AuthorityInfo]
  CHECK CONSTRAINT [FK_RoleInfo_AuthorityInfo_AuthorityInfo]
GO
/****** Object:  ForeignKey [FK_RoleInfo_AuthorityInfo_RoleInfo]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[RoleInfo_AuthorityInfo]
  WITH CHECK ADD CONSTRAINT [FK_RoleInfo_AuthorityInfo_RoleInfo] FOREIGN KEY ([roleId])
REFERENCES [dbo].[RoleInfo] ([id])
GO
ALTER TABLE [dbo].[RoleInfo_AuthorityInfo]
  CHECK CONSTRAINT [FK_RoleInfo_AuthorityInfo_RoleInfo]
GO
/****** Object:  ForeignKey [FK_CargoLocationType_WareHouse]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[CargoLocationType]
  WITH CHECK ADD CONSTRAINT [FK_CargoLocationType_WareHouse] FOREIGN KEY ([wareHouseId])
REFERENCES [dbo].[WareHouse] ([id])
GO
ALTER TABLE [dbo].[CargoLocationType]
  CHECK CONSTRAINT [FK_CargoLocationType_WareHouse]
GO
/****** Object:  ForeignKey [FK_CargoLocation_Data_CargoLocation_Data]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[CargoLocation_Data]
  WITH CHECK ADD CONSTRAINT [FK_CargoLocation_Data_CargoLocation_Data] FOREIGN KEY ([parentId])
REFERENCES [dbo].[CargoLocation_Data] ([id])
GO
ALTER TABLE [dbo].[CargoLocation_Data]
  CHECK CONSTRAINT [FK_CargoLocation_Data_CargoLocation_Data]
GO
/****** Object:  ForeignKey [FK_CargoLocation_Data_WareHouse]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[CargoLocation_Data]
  WITH CHECK ADD CONSTRAINT [FK_CargoLocation_Data_WareHouse] FOREIGN KEY ([wareHouseId])
REFERENCES [dbo].[WareHouse] ([id])
GO
ALTER TABLE [dbo].[CargoLocation_Data]
  CHECK CONSTRAINT [FK_CargoLocation_Data_WareHouse]
GO
/****** Object:  ForeignKey [FK_CargoLocation_Data_WareHouseConfig]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[CargoLocation_Data]
  WITH CHECK ADD CONSTRAINT [FK_CargoLocation_Data_WareHouseConfig] FOREIGN KEY ([configId])
REFERENCES [dbo].[WareHouseConfig] ([id])
GO
ALTER TABLE [dbo].[CargoLocation_Data]
  CHECK CONSTRAINT [FK_CargoLocation_Data_WareHouseConfig]
GO
/****** Object:  ForeignKey [FK_CargoLocation_CargoLocationType]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[CargoLocation]
  WITH CHECK ADD CONSTRAINT [FK_CargoLocation_CargoLocationType] FOREIGN KEY ([typeId])
REFERENCES [dbo].[CargoLocationType] ([id])
GO
ALTER TABLE [dbo].[CargoLocation]
  CHECK CONSTRAINT [FK_CargoLocation_CargoLocationType]
GO
/****** Object:  ForeignKey [FK_CargoLocation_WareHouse]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[CargoLocation]
  WITH CHECK ADD CONSTRAINT [FK_CargoLocation_WareHouse] FOREIGN KEY ([wareHouseId])
REFERENCES [dbo].[WareHouse] ([id])
GO
ALTER TABLE [dbo].[CargoLocation]
  CHECK CONSTRAINT [FK_CargoLocation_WareHouse]
GO
/****** Object:  ForeignKey [FK_Material_CargoLocation]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[Material]
  WITH CHECK ADD CONSTRAINT [FK_Material_CargoLocation] FOREIGN KEY ([cargoLocationId])
REFERENCES [dbo].[CargoLocation] ([id])
GO
ALTER TABLE [dbo].[Material]
  CHECK CONSTRAINT [FK_Material_CargoLocation]
GO
/****** Object:  ForeignKey [FK_Material_CargoLocationType]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[Material]
  WITH CHECK ADD CONSTRAINT [FK_Material_CargoLocationType] FOREIGN KEY ([cargoLocationTypeId])
REFERENCES [dbo].[CargoLocationType] ([id])
GO
ALTER TABLE [dbo].[Material]
  CHECK CONSTRAINT [FK_Material_CargoLocationType]
GO
/****** Object:  ForeignKey [FK_Material_Material]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[Material]
  WITH CHECK ADD CONSTRAINT [FK_Material_Material] FOREIGN KEY ([id])
REFERENCES [dbo].[Material] ([id])
GO
ALTER TABLE [dbo].[Material]
  CHECK CONSTRAINT [FK_Material_Material]
GO
/****** Object:  ForeignKey [FK_Material_WareHouse]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[Material]
  WITH CHECK ADD CONSTRAINT [FK_Material_WareHouse] FOREIGN KEY ([wareHouseId])
REFERENCES [dbo].[WareHouse] ([id])
GO
ALTER TABLE [dbo].[Material]
  CHECK CONSTRAINT [FK_Material_WareHouse]
GO
/****** Object:  ForeignKey [FK_Material_CargoLocationType_Data_CargoLocationType]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[Material_CargoLocationType_Data]
  WITH CHECK ADD CONSTRAINT [FK_Material_CargoLocationType_Data_CargoLocationType] FOREIGN KEY ([cargoLocationTypeId])
REFERENCES [dbo].[CargoLocationType] ([id])
GO
ALTER TABLE [dbo].[Material_CargoLocationType_Data]
  CHECK CONSTRAINT [FK_Material_CargoLocationType_Data_CargoLocationType]
GO
/****** Object:  ForeignKey [FK_Material_CargoLocationType_Data_Material]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[Material_CargoLocationType_Data]
  WITH CHECK ADD CONSTRAINT [FK_Material_CargoLocationType_Data_Material] FOREIGN KEY ([materialId])
REFERENCES [dbo].[Material] ([id])
GO
ALTER TABLE [dbo].[Material_CargoLocationType_Data]
  CHECK CONSTRAINT [FK_Material_CargoLocationType_Data_Material]
GO
/****** Object:  ForeignKey [FK_Material_CargoLocationType_Data_WareHouse]    Script Date: 05/12/2017 14:23:45 ******/
ALTER TABLE [dbo].[Material_CargoLocationType_Data]
  WITH CHECK ADD CONSTRAINT [FK_Material_CargoLocationType_Data_WareHouse] FOREIGN KEY ([wareHouseId])
REFERENCES [dbo].[WareHouse] ([id])
GO
ALTER TABLE [dbo].[Material_CargoLocationType_Data]
  CHECK CONSTRAINT [FK_Material_CargoLocationType_Data_WareHouse]
GO
