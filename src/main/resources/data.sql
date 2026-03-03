--     *** some items ***
insert into items(title, description, img_path, price)
with src as (
    values
        ('Title 1', 'Some description 1', 'images/1.jpeg', 1),
        ('Title 2', 'Some description 2', 'images/2.jpeg', 2),
        ('Title 3', 'Some description 3', 'images/3.jpeg', 3),
        ('Title 4', 'Some description 4', 'images/4.jpeg', 4),
        ('Title 5', 'Some description 5', 'images/5.jpeg', 5)
)
select *
  from src
  where not exists(select 1 from items);