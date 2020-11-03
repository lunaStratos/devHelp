프로시져 만드는 방법 

1. 프로시져로 만들 쿼리를 뽑아온다. 

2. 쿼리를 CREATE PROC으로 감싼다

3. 파라메터를 넣는다.

/* 이 안에 쿼리를 넣으면 됨 */

/* 일반쿼리 만들때 쿼리형태 */
CREATE PROC [dbo].[프로시져이름]
(
@value1 NVARCHAR(200)
)
AS
BEGIN

SET NOCOUNT ON 
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;


	BEGIN
		select sysdate() as getdate
	END
END;


5. mybatis에서 프로시져를 호출할때는 다음과 같이 한다

<select id="selectSample" parameterType="map" resultType="map" statementType="CALLABLE">
{
call dbo.프로시져이름(
#{value1}
)
}
</select> 

statementType="CALLABLE" 을 넣어야 한다.
그리고 call 안에 들어가는 텍스트에는 tab 버튼이 있으면 안된다. 



/* 예제 */

/* 일반쿼리 만들때 쿼리형태 */
CREATE PROC [dbo].[프로시져이름]
(
@value1 NVARCHAR(200)
, @nullvalue2 NVARCHAR(200)  = ''  -- null일때 기본값 지정  
)
AS
BEGIN

SET NOCOUNT ON 
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;


	BEGIN
		select * from sample_table
		where value1 = @value1
	END
END;


/* 동적쿼리를 만들때 쿼리형태 */
CREATE PROC [dbo].[프로시져이름]
(
@value1 NVARCHAR(200)
, @nullvalue2 NVARCHAR(200)  = ''  -- null일때 기본값 지정  
)
AS
BEGIN

SET NOCOUNT ON 
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;


	BEGIN
	/* 쿼리시작 */
		Declare @sql_select nvarchar(4000)
				, @sql_where nvarchar(4000) -- 변수명 선언
				
		set @sql_select = 'update dbo.sample_table set use_yn = '''+'M'+''' where value1 = ' + @value1 +' '
		-- '''의 경우 String을 표현하기 위해서 사용. "" 안먹힘 
		
		print @value2 -- 로그표시용, 삭제해도 됨
		
		-- 동적쿼리 만들때 이렇게 만듬 
		if(ISNULL(@value1, '') = '')
			BEGIN
				set @sql_where = ''
			END
		ELSE
			BEGIN
				set @sql_where = 'AND value1 = ''' + @value1 + ''' '
			END
			
		print @sql_select + @sql_where -- 쿼리 String 생성 
		
		exec (@sql_select + @sql_where) -- 쿼리실행
	/* 쿼리종료 */
	END
END;




/* insert 쿼리 (select 자동으로 넣기 )  */
CREATE PROC [dbo].[프로시져이름]
(
@value1 NVARCHAR(200)
, @nullvalue2 NVARCHAR(200)  = ''  -- null일때 기본값 지정  
)
AS
BEGIN

SET NOCOUNT ON 
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;

	
	BEGIN
		insert into sample_table(
		value1
		, value2
		, value3
		, value4
		)
		select 
		value1
		, value2
		, value3
		, value4
			from sample_table
		where value1 = @value1
	END
END;




/* insert 쿼리  */
CREATE PROC [dbo].[프로시져이름]
(
@value1 NVARCHAR(200)
, @value2 NVARCHAR(200)
, @value3 NVARCHAR(200)
, @value4 NVARCHAR(200)
, @nullvalue2 NVARCHAR(200)  = ''  -- null일때 기본값 지정  
)
AS
BEGIN

SET NOCOUNT ON 
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;

	
	BEGIN
		insert into sample_table(
		value1
		, value2
		, value3
		, value4
		)
		select 
		@value1
		, @value2
		, @value3
		, @value4
			from sample_table
		where value1 = @value1
	END
END;
	
	
