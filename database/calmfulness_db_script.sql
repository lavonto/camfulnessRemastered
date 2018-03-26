USE [calmfulness-remastered]
GO
/****** Object:  Table [dbo].[exercise]    Script Date: 26.3.2018 16.46.39 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[exercise](
	[id] [nvarchar](255) NOT NULL,
	[createdAt] [datetimeoffset](3) NOT NULL,
	[updatedAt] [datetimeoffset](3) NULL,
	[version] [timestamp] NOT NULL,
	[deleted] [bit] NULL,
	[title_fi] [nvarchar](max) NOT NULL,
	[title_en] [nvarchar](max) NOT NULL,
	[text_fi] [nvarchar](max) NOT NULL,
	[text_en] [nvarchar](max) NOT NULL,
	[picture_url] [nvarchar](max) NULL,
	[video_url] [nvarchar](max) NULL,
 CONSTRAINT [PK__exercise__3213E83E9F8ADFD3] PRIMARY KEY NONCLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[location]    Script Date: 26.3.2018 16.46.39 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[location](
	[id] [nvarchar](255) NOT NULL,
	[createdAt] [datetimeoffset](3) NOT NULL,
	[updatedAt] [datetimeoffset](3) NULL,
	[version] [timestamp] NOT NULL,
	[deleted] [bit] NULL,
	[lat] [float] NOT NULL,
	[lon] [float] NOT NULL,
	[impactrange] [tinyint] NOT NULL,
 CONSTRAINT [PK__location__3213E83EBD4DEFC2] PRIMARY KEY NONCLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[location_exercise]    Script Date: 26.3.2018 16.46.39 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[location_exercise](
	[id] [nvarchar](255) NOT NULL,
	[createdAt] [datetimeoffset](3) NOT NULL,
	[updatedAt] [datetimeoffset](3) NULL,
	[version] [timestamp] NOT NULL,
	[deleted] [bit] NULL,
	[exercise] [nvarchar](255) NOT NULL,
	[location] [nvarchar](255) NOT NULL,
 CONSTRAINT [PK__location__3213E83EB07416E6] PRIMARY KEY NONCLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[route]    Script Date: 26.3.2018 16.46.40 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[route](
	[id] [nvarchar](255) NOT NULL,
	[createdAt] [datetimeoffset](3) NOT NULL,
	[updatedAt] [datetimeoffset](3) NULL,
	[version] [timestamp] NOT NULL,
	[deleted] [bit] NULL,
	[file] [nvarchar](255) NOT NULL,
	[name_fi] [nvarchar](255) NOT NULL,
	[name_en] [nvarchar](255) NOT NULL,
 CONSTRAINT [PK__route__3213E83E39493AFF] PRIMARY KEY NONCLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_route_file] UNIQUE NONCLUSTERED 
(
	[file] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_route_name_en] UNIQUE NONCLUSTERED 
(
	[name_en] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_route_name_fi] UNIQUE NONCLUSTERED 
(
	[name_fi] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[exercise] ADD  CONSTRAINT [DF_exercise_id]  DEFAULT (CONVERT([nvarchar](255),newid(),(0))) FOR [id]
GO
ALTER TABLE [dbo].[exercise] ADD  CONSTRAINT [DF_exercise_createdAt]  DEFAULT (CONVERT([datetimeoffset](3),sysutcdatetime(),(0))) FOR [createdAt]
GO
ALTER TABLE [dbo].[exercise] ADD  CONSTRAINT [DF__exercise__delete__52593CB8]  DEFAULT ((0)) FOR [deleted]
GO
ALTER TABLE [dbo].[location] ADD  CONSTRAINT [DF_location_id]  DEFAULT (CONVERT([nvarchar](255),newid(),(0))) FOR [id]
GO
ALTER TABLE [dbo].[location] ADD  CONSTRAINT [DF_location_createdAt]  DEFAULT (CONVERT([datetimeoffset](3),sysutcdatetime(),(0))) FOR [createdAt]
GO
ALTER TABLE [dbo].[location] ADD  CONSTRAINT [DF__location__delete__5812160E]  DEFAULT ((0)) FOR [deleted]
GO
ALTER TABLE [dbo].[location_exercise] ADD  CONSTRAINT [DF_location_exercise_id]  DEFAULT (CONVERT([nvarchar](255),newid(),(0))) FOR [id]
GO
ALTER TABLE [dbo].[location_exercise] ADD  CONSTRAINT [DF_location_exercise_createdAt]  DEFAULT (CONVERT([datetimeoffset](3),sysutcdatetime(),(0))) FOR [createdAt]
GO
ALTER TABLE [dbo].[location_exercise] ADD  CONSTRAINT [DF__location___delet__00200768]  DEFAULT ((0)) FOR [deleted]
GO
ALTER TABLE [dbo].[route] ADD  CONSTRAINT [DF_route_id]  DEFAULT (CONVERT([nvarchar](255),newid(),(0))) FOR [id]
GO
ALTER TABLE [dbo].[route] ADD  CONSTRAINT [DF_route_createdAt]  DEFAULT (CONVERT([datetimeoffset](3),sysutcdatetime(),(0))) FOR [createdAt]
GO
ALTER TABLE [dbo].[route] ADD  CONSTRAINT [DF__route__deleted__4CA06362]  DEFAULT ((0)) FOR [deleted]
GO
ALTER TABLE [dbo].[location_exercise]  WITH CHECK ADD  CONSTRAINT [FK_location_exercise_exercise] FOREIGN KEY([exercise])
REFERENCES [dbo].[exercise] ([id])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[location_exercise] CHECK CONSTRAINT [FK_location_exercise_exercise]
GO
ALTER TABLE [dbo].[location_exercise]  WITH CHECK ADD  CONSTRAINT [FK_location_exercise_location] FOREIGN KEY([location])
REFERENCES [dbo].[location] ([id])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[location_exercise] CHECK CONSTRAINT [FK_location_exercise_location]
GO
