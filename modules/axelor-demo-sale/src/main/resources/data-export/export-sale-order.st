<?xml version="1.0" encoding="utf-8"?>
<sale-order>
	<id>$name$</id>
	<date>$orderDate$</date>
	<customer>$customer.fullName; format="escape"$</customer>
$items: { it |
	<item>
		<product>$it.product.name$</product>
		$! if selection name is defined in domain object, no need to specify selection format !$
		$if(it.product.color)$<color>$it.product.color; format="selection:product.color.selection"$</color>$endif$
		<price>$it.price$</price>
		<quantity>$it.quantity$</quantity>
	</item>
}$
</sale-order>